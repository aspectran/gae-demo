/*
 * Copyright (c) 2008-2018 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.demo.tts;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.ablility.DisposableBean;
import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.core.util.ToStringBuilder;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Synthesizes speech from text for immediate playback or
 * sends synthesized sound data to an output stream of bytes.
 *
 * <p>Created: 2018. 8. 29.</p>
 */
public class TextToSpeechBean implements InitializableBean, DisposableBean {

    private static final Log log = LogFactory.getLog(TextToSpeechBean.class);

    private String voicePackage;

    private String voiceName;

    private Voice voice;

    private Float rate = 150.0F;

    private Float pitch = 100.0F;

    private Float pitchRange = 12.0F;

    public void setVoicePackage(String voicePackage) {
        this.voicePackage = voicePackage;
        System.setProperty("freetts.voices", voicePackage);
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPitchRange(float pitchRange) {
        this.pitchRange = pitchRange;
    }

    public void setProperties(Properties properties) {
        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            System.setProperty(key, properties.getProperty(key));
        }
    }

    @Override
    public void initialize() {
        if (voicePackage == null) {
            setVoicePackage("com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        }

        VoiceManager voiceManager = VoiceManager.getInstance();

        if (log.isDebugEnabled()) {
            Voice[] voices = voiceManager.getVoices();
            String[] arr = new String[voices.length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = voices[i].getName() + "(" + voices[i].getDomain() + " domain)";
            }
            ToStringBuilder tsb = new ToStringBuilder("All voices available");
            tsb.append("voices", arr);
            log.debug(tsb.toString());
        }

        if (voiceName != null) {
            voice = voiceManager.getVoice(voiceName);
        } else {
            Voice[] voices = voiceManager.getVoices();
            if (voices != null && voices.length > 0) {
                voice = voices[0];
            }
        }
        if (voice != null) {
            voice.setRate(rate);
            voice.setPitch(pitch);
            voice.setPitchRange(pitchRange);
            voice.allocate();
        }
    }

    @Override
    public void destroy() {
        if (voice != null) {
            voice.deallocate();
            voice = null;
        }
    }

    /**
     * Synthesizes speech of the given text and plays immediately.
     *
     * @param text the text that will be transformed to speech
     */
    public void speak(String text) {
        if (voice == null) {
            throw new IllegalStateException("Cannot find a voice named " + voiceName);
        }
        voice.speak(text);
    }

    public synchronized ByteArrayAudioPlayer getAudioPlayer(String text) {
        if (voice == null) {
            throw new IllegalStateException("Cannot find a voice named " + voiceName);
        }
        AudioPlayer oldAudioPlayer = voice.getAudioPlayer();
        ByteArrayAudioPlayer audioPlayer = new ByteArrayAudioPlayer();
        voice.setAudioPlayer(audioPlayer);
        voice.speak(text);
        voice.setAudioPlayer(oldAudioPlayer);
        return audioPlayer;
    }

    public synchronized AudioInputStream getAudioInputStream(String text) {
        if (voice == null) {
            throw new IllegalStateException("Cannot find a voice named " + voiceName);
        }
        ByteArrayAudioPlayer audioPlayer = getAudioPlayer(text);
        return audioPlayer.getAudioInputStream();
    }

    /**
     * Sends synthesized sound data to an output stream of bytes.
     *
     * @param text the text that will be transformed to speech
     * @param out the output stream of bytes
     * @throws IOException
     */
    public synchronized void speak(String text, OutputStream out) throws IOException {
        AudioInputStream ais = getAudioInputStream(text);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
    }

    public void speak(Translet translet) throws IOException {
        translet.getResponseAdapter().setHeader("Content-Type", "audio/vnd.wav");
        translet.getResponseAdapter().setHeader("Content-Disposition", "attachment; filename=\"output.wav\"");
        String text = translet.getParameter("text");
        if (text != null && text.length() > 0) {
            ByteArrayAudioPlayer audioPlayer = getAudioPlayer(text);
            int bytes = audioPlayer.getTotalBytes();
            translet.getResponseAdapter().setHeader("Content-Range", "bytes 0-" + (bytes - 1) + "/" + (bytes - 1));
            translet.getResponseAdapter().setHeader("Content-Length", Integer.toString(audioPlayer.getTotalBytes()));
            translet.getResponseAdapter().setHeader("Accept-Ranges", "bytes");
            AudioInputStream ais = audioPlayer.getAudioInputStream();
            OutputStream out = translet.getResponseAdapter().getOutputStream();
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
        }
        translet.getResponseAdapter().setStatus(200);
    }

    public static void main(String[] args) {
        TextToSpeechBean textToSpeechBean = new TextToSpeechBean();
        textToSpeechBean.setVoiceName("kevin16");
        textToSpeechBean.initialize();
        textToSpeechBean.speak("test");
        textToSpeechBean.destroy();
    }

}
