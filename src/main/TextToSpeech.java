package main;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.datatypes.MaryXML;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.AudioEffect;
import marytts.signalproc.effects.AudioEffects;
import marytts.util.data.audio.MaryAudioUtils;

public class TextToSpeech {
	
	private static final String ADULT_FEMALE = "dfki-poppy-hsmm";
	private static final String ADULT_MALE = "cmu-rms-hsmm";
	private static final String TYPE_FEMALE = "female";
	private static final String TYPE_MALE = "male";
	
	private AudioPlayer tts;
	private MaryInterface marytts;
	private long captchaNum;
	private boolean first;
	
	
	public TextToSpeech() {
		try {
			marytts = new LocalMaryInterface();
			
		} catch (MaryConfigurationException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
		}
		captchaNum = 0;
		first = true;
	}
	
	public String generateWav(String text, VoiceType voice, int rate, int freq, boolean high) {
		String fileName = voice.getType() + text + ".wav";
		setVoice(voice.getVoice());
		
		marytts.setInputType("RAWMARYXML");
		Document document = MaryXML.newDocument();
		Element maryxml = document.getDocumentElement();
		maryxml.setAttribute("xml:lang", "en-US");
		Element paragraph = MaryXML.appendChildElement(maryxml, MaryXML.PARAGRAPH);
		Element prosody = MaryXML.appendChildElement(paragraph, MaryXML.PROSODY);
		prosody.setAttribute("rate", rate + "%");
		if(high) {
			prosody.setAttribute("pitch", "+" + freq + "%");
		}
		else {
			prosody.setAttribute("pitch", "-" + freq + "%");
		}
		prosody.setTextContent(text);
		MaryData maryData = new MaryData(MaryDataType.PHONEMES, Locale.ENGLISH, false);
		maryData.setDocument(document);
		try (AudioInputStream audio = marytts.generateAudio(document)) {	
			MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), "hello.wav", audio.getFormat());
			} catch (SynthesisException ex) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error saying phrase.", ex);
			} catch (IOException ex) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, "IO Exception", ex);
			}
		
		if(!first) {
			captchaNum++;
		}
		
		first = !first;
		return fileName;
	}

	public void appendWav(String wavFile1, String wavFile2, String newname, boolean pre) {
        try {
           	AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(wavFile1));
           	AudioInputStream clip2 = AudioSystem.getAudioInputStream(new File(wavFile2));
           	AudioFormat f;
           	
           	if(pre) {
           		f = clip2.getFormat();
           	}
           	else {
           		f = clip1.getFormat();
           	}

            AudioInputStream appendedFiles = 
                            new AudioInputStream(
                                new SequenceInputStream(clip1, clip2), f, clip1.getFrameLength() + clip2.getFrameLength());
            AudioSystem.write(appendedFiles, AudioFileFormat.Type.WAVE, new File(newname + ".wav"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//multiples of 10ms
	public void addPreSilenceMs(String wavFile, int time) {
		if(time>1) {
			appendWav("silence.wav", "silence.wav", "longsilence", true);
			for(int i = 0; i < time-2; i++) {
				appendWav("silence.wav", "longsilence.wav", "longsilence", true);
			}
			appendWav("longsilence.wav", wavFile, "wavFile_"+ time + "silence", true);
		}
		else {
			appendWav("silence.wav", wavFile, "wavFile_"+ time + "silence", true);
		}
	}
	
	//multiples of 0.1s
	public void addPreSilence(String wavFile, int time, String newname) {
		int seq = 0;
		int seq2 = 1;
		if(time>1) {
			appendWav("silence010.wav", "silence010.wav", "longsilence" + seq, true);
			for(int i = 0; i < time-2; i++) {
				seq = (seq == 0) ? 1 : 0;
				seq2 = (seq2 == 0) ? 1 : 0;
				appendWav("silence010.wav", "longsilence" + seq2 + ".wav", "longsilence" + seq, true);
			}
			appendWav("longsilence" + seq + ".wav", wavFile, newname, true);
		}
		else {
			appendWav("silence010.wav", wavFile, newname, true);
		}
	}
	
	public void addPostSilence(String wavFile, int time, String newname) {
		int seq = 0;
		int seq2 = 1;
		if(time>1) {
			appendWav("silence010.wav", "silence010.wav", "longsilence" + seq, false);
			for(int i = 0; i < time-2; i++) {
				appendWav("silence010.wav", "longsilence" + seq2 + ".wav", "longsilence" + seq, false);
			}
			appendWav(wavFile, "longsilence" + seq + ".wav", newname, false);
		}
		else {
			appendWav(wavFile, "silence010.wav", newname, false);
		}
	}

	public void changeFreq(int rate, int freq, boolean high) {
		String text = "Hello World";
		marytts.setInputType("RAWMARYXML");
		marytts.setVoice(ADULT_FEMALE);
		Document document = MaryXML.newDocument();
		Element maryxml = document.getDocumentElement();
		maryxml.setAttribute("xml:lang", "en-US");
		Element paragraph = MaryXML.appendChildElement(maryxml, MaryXML.PARAGRAPH);
		Element prosody = MaryXML.appendChildElement(paragraph, MaryXML.PROSODY);
		prosody.setAttribute("rate", rate + "%");
		if(high) {
			prosody.setAttribute("pitch", "+" + freq + "%");
		}
		else {
			prosody.setAttribute("pitch", "-" + freq + "%");
		}
		prosody.setTextContent(text);
		MaryData maryData = new MaryData(MaryDataType.PHONEMES, Locale.ENGLISH, false);
		maryData.setDocument(document);
		try (AudioInputStream audio = marytts.generateAudio(document)) {	
			MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), "hello.wav", audio.getFormat());
			} catch (SynthesisException ex) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error saying phrase.", ex);
			} catch (IOException ex) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, "IO Exception", ex);
			}
		
	}
	
	public Collection<Voice> getAvailableVoices() {
		return Voice.getAvailableVoices();
	}
	
	public MaryInterface getMarytts() {
		return marytts;
	}
	
	public List<AudioEffect> getAudioEffects() {
		return StreamSupport.stream(AudioEffects.getEffects().spliterator(), false).collect(Collectors.toList());
	}
	
	public void setVoice(String voice) {
		marytts.setVoice(voice);
	}
	
}
