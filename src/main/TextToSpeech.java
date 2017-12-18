package main;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.AudioEffect;
import marytts.signalproc.effects.AudioEffects;
import marytts.signalproc.process.AudioFileMixer;
import marytts.signalproc.process.AudioMixer;
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
	
	public String generateWav(String text, VoiceType voice) {
		String fileName = voice.getType() + String.valueOf(captchaNum) + ".wav";
		setVoice(voice.getVoice());
		
		try (AudioInputStream audio = marytts.generateAudio(text)) {	
		MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), fileName, audio.getFormat());
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
	
	public void speak(String filename, float gainValue , boolean daemon , boolean join) {
		
		try (AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filename))) {
			
			// Player is a thread(threads can only run one time) so it can be
			// used has to be initiated every time
			tts = new AudioPlayer();
			tts.setAudio(audio);
			tts.setGain(gainValue);
			tts.setDaemon(daemon);
			tts.start();
			if (join)
				tts.join();
			
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "IO Exception", ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Interrupted ", ex);
			tts.interrupt();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopSpeaking() {
		// Stop the previous player
		if (tts != null)
			tts.cancel();
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
