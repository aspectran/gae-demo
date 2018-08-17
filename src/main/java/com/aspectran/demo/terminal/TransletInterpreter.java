package com.aspectran.demo.terminal;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Configuration;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.core.component.bean.annotation.Transform;
import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.component.translet.TransletNotFoundException;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.context.rule.type.TransformType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.json.JsonWriter;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration(namespace = "/terminal")
public class TransletInterpreter implements ActivityContextAware {

    private static final String COMMAND_PREFIX = "/terminal/";

    private ActivityContext context;

    @Override
    public void setActivityContext(ActivityContext context) {
        this.context = context;
    }

    @Request(translet = "/exec")
    @Transform(type = TransformType.TEXT, contentType = "application/json")
    public void execute(Translet translet) throws IOException, InvocationTargetException {
        String transletName = translet.getParameter("_translet");
        boolean force = Boolean.parseBoolean(translet.getParameter("_force"));
        if (StringUtils.isEmpty(transletName)) {
            return;
        }

        String transletFullName = COMMAND_PREFIX + transletName;

        TransletRule transletRule = context.getTransletRuleRegistry().getTransletRule(transletFullName);
        if (transletRule == null) {
            throw new TransletNotFoundException(transletName);
        }

        ItemRuleMap parameterItemRuleMap = transletRule.getRequestRule().getParameterItemRuleMap();
        ItemRuleMap attributeItemRuleMap = transletRule.getRequestRule().getAttributeItemRuleMap();

        Writer writer = translet.getResponseAdapter().getWriter();

        writer.write("{");
        writer.write("\"translet\": ");
        new JsonWriter(writer).write(toMap(transletRule));

        writer.write(", \"request\": {");
        if (parameterItemRuleMap != null) {
            writer.write("\"parameters\": ");
            new JsonWriter(writer).write(toList(parameterItemRuleMap));
        }
        if (attributeItemRuleMap != null) {
            if (parameterItemRuleMap != null) {
                writer.write(", ");
            }
            writer.write("\"attributes\": ");
            new JsonWriter(writer).write(toList(attributeItemRuleMap));
        }
        writer.write("}");

        writer.write(", \"contentType\": ");
        writer.write("\"");
        writer.write(transletRule.getResponseRule().getResponse().getContentType());
        writer.write("\"");

        if (force || (parameterItemRuleMap == null && attributeItemRuleMap == null)) {
            writer.write(", \"response\": ");
            performActivity(transletRule);
        }

        writer.write("}");
    }

    private void performActivity(TransletRule transletRule) {
        Activity activity = context.getCurrentActivity().newActivity();
        activity.prepare(transletRule);
        activity.perform();
    }

    private Map<String, String> toMap(TransletRule transletRule) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", transletRule.getName());
        map.put("description", transletRule.getDescription());
        return map;
    }

    private List<Map<String, Object>> toList(ItemRuleMap itemRuleMap) {
        List<Map<String, Object>> list = new ArrayList<>();
        Collection<ItemRule> itemRuleList = itemRuleMap.values();
        Map<Token, Set<ItemRule>> inputTokens = new LinkedHashMap<>();
        for (ItemRule itemRule : itemRuleList) {
            Token[] tokens = itemRule.getAllTokens();
            if (tokens == null || tokens.length == 0) {
                Token t = new Token(TokenType.PARAMETER, itemRule.getName());
                tokens = new Token[] { t };
            }
            for (Token t1 : tokens) {
                if (t1.getType() == TokenType.PARAMETER) {
                    boolean exists = false;
                    for (Token t2 : inputTokens.keySet()) {
                        if (t2.equals(t1)) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        Set<ItemRule> rules = inputTokens.get(t1);
                        rules.add(itemRule);
                    } else {
                        Set<ItemRule> rules = new LinkedHashSet<>();
                        rules.add(itemRule);
                        inputTokens.put(t1, rules);
                    }
                }
            }
        }
        for (Map.Entry<Token, Set<ItemRule>> entry : inputTokens.entrySet()) {
            Token token = entry.getKey();
            Set<ItemRule> rules = entry.getValue();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", token.getName());
            map.put("defaultValue", token.getDefaultValue());
            map.put("string", token.stringify());

            boolean security = false;
            for (ItemRule ir : rules) {
                if (ir.isSecurity()) {
                    security = true;
                    break;
                }
            }
            map.put("security", security);

            boolean mandatory = false;
            for (ItemRule ir : rules) {
                if (ir.isMandatory()) {
                    mandatory = true;
                    break;
                }
            }
            map.put("mandatory", mandatory);

            map.put("itemRules", toList(rules));
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> toList(Set<ItemRule> itemRules) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ItemRule itemRule : itemRules) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", itemRule.getType().toString());
            map.put("name", itemRule.getName());
            map.put("value", itemRule.getValue());
            map.put("defaultValue", itemRule.getDefaultValue());
            map.put("mandatory", itemRule.isMandatory());
            map.put("security", itemRule.isSecurity());
            list.add(map);
        }
        return list;
    }

}