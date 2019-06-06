package com.aspectran.demosite.terminal;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.activity.TransletNotFoundException;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import com.aspectran.core.component.bean.annotation.Transform;
import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.expr.token.TokenParser;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.context.rule.type.TransformType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.json.JsonWriter;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component(namespace = "/terminal")
@Bean(id = "transletInterpreter")
public class TransletInterpreter implements ActivityContextAware {

    private static final Log log = LogFactory.getLog(TransletInterpreter.class);

    private static final String COMMAND_PREFIX = "/terminal/";

    private ActivityContext context;

    @Override
    public void setActivityContext(ActivityContext context) {
        this.context = context;
    }

    @RequestToGet("/query/@{_translet_}")
    @Transform(type = TransformType.TEXT, contentType = "application/json")
    public void query(Translet translet) throws IOException {
        String transletName = translet.getAttribute("_translet_");
        if (StringUtils.isEmpty(transletName)) {
            return;
        }

        String transletFullName = COMMAND_PREFIX + transletName;

        TransletRule transletRule = context.getTransletRuleRegistry().getTransletRule(transletFullName);
        if (transletRule == null) {
            if (log.isDebugEnabled()) {
                log.debug("Translet not found: " + transletFullName);
            }

            JsonWriter jsonWriter = new JsonWriter(translet.getResponseAdapter().getWriter());
            jsonWriter.openCurlyBracket();
            jsonWriter.writeName("translet");
            jsonWriter.writeNull();
            jsonWriter.writeName("request");
            jsonWriter.writeNull();
            jsonWriter.writeName("response");
            jsonWriter.writeNull();
            jsonWriter.closeCurlyBracket();
            return;
        }

        ItemRuleMap parameterItemRuleMap = transletRule.getRequestRule().getParameterItemRuleMap();
        ItemRuleMap attributeItemRuleMap = transletRule.getRequestRule().getAttributeItemRuleMap();

        JsonWriter jsonWriter = new JsonWriter(translet.getResponseAdapter().getWriter());
        jsonWriter.openCurlyBracket();
        jsonWriter.writeName("translet");
        jsonWriter.write(toMap(transletRule));
        jsonWriter.writeComma();
        jsonWriter.writeName("request");
        jsonWriter.openCurlyBracket();
        if (parameterItemRuleMap != null) {
            jsonWriter.writeName("parameters");
            jsonWriter.openCurlyBracket();
            jsonWriter.writeName("items");
            jsonWriter.write(toListForItems(parameterItemRuleMap.values()));
            jsonWriter.writeComma();
            jsonWriter.writeName("tokens");
            jsonWriter.write(toListForTokens(parameterItemRuleMap.values()));
            jsonWriter.closeCurlyBracket();
        }
        if (attributeItemRuleMap != null) {
            if (parameterItemRuleMap != null) {
                jsonWriter.writeComma();
            }
            jsonWriter.writeName("attributes");
            jsonWriter.openCurlyBracket();
            jsonWriter.writeName("items");
            jsonWriter.write(toListForItems(attributeItemRuleMap.values()));
            jsonWriter.writeComma();
            jsonWriter.writeName("tokens");
            jsonWriter.write(toListForTokens(attributeItemRuleMap.values()));
            jsonWriter.closeCurlyBracket();
        }
        jsonWriter.closeCurlyBracket();
        jsonWriter.writeComma();
        jsonWriter.writeName("response");
        jsonWriter.openCurlyBracket();
        if (transletRule.getResponseRule().getResponse() != null) {
            jsonWriter.writeName("contentType");
            jsonWriter.writeValue(transletRule.getResponseRule().getResponse().getContentType());
        }
        jsonWriter.closeCurlyBracket();
        jsonWriter.closeCurlyBracket();
    }

    @RequestToPost("/exec/@{_translet_}")
    public void execute(Translet translet) {
        String transletName = translet.getAttribute("_translet_");
        if (StringUtils.isEmpty(transletName)) {
            return;
        }

        String transletFullName = COMMAND_PREFIX + transletName;

        TransletRule transletRule = context.getTransletRuleRegistry().getTransletRule(transletFullName);
        if (transletRule == null) {
            throw new TransletNotFoundException(transletName);
        }

        performActivity(transletFullName, transletRule);
    }

    private void performActivity(String transletName, TransletRule transletRule) {
        Activity activity = context.getCurrentActivity().newActivity();
        activity.prepare(transletName, transletRule);
        activity.perform();
    }

    private Map<String, String> toMap(TransletRule transletRule) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", transletRule.getName());
        map.put("description", transletRule.getDescription());
        return map;
    }

    private List<Map<String, Object>> toListForTokens(Collection<ItemRule> itemRules) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<Token, Set<ItemRule>> inputTokens = new LinkedHashMap<>();
        for (ItemRule itemRule : itemRules) {
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

            boolean secret = false;
            for (ItemRule ir : rules) {
                if (ir.isSecret()) {
                    secret = true;
                    break;
                }
            }
            map.put("secret", secret);

            boolean mandatory = false;
            for (ItemRule ir : rules) {
                if (ir.isMandatory()) {
                    mandatory = true;
                    break;
                }
            }
            map.put("mandatory", mandatory);

            map.put("items", toListForItems(rules));
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> toListForItems(Collection<ItemRule> itemRules) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ItemRule itemRule : itemRules) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", itemRule.getType().toString());
            map.put("name", itemRule.getName());
            map.put("value", itemRule.getValue());
            map.put("mandatory", itemRule.isMandatory());
            map.put("secret", itemRule.isSecret());

            Token[] tokens = itemRule.getAllTokens();
            if (tokens == null) {
                Token t = new Token(TokenType.PARAMETER, itemRule.getName());
                tokens = new Token[] { t };
            }
            map.put("tokenString", TokenParser.toString(tokens));

            String[] tokenNames = new String[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                tokenNames[i] = tokens[i].getName();
            }
            map.put("tokenNames", tokenNames);

            list.add(map);
        }
        return list;
    }

}