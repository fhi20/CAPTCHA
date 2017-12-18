package main;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.UnsupportedAudioFileException;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;
import marytts.signalproc.process.AudioFileMixer;

public class CaptchaCreator {
	private VoiceType adultM;
	private VoiceType adultF;
	private TextToSpeech tts;
	private Random rand;
	private String answer;
	public String listenFor;
	double bgGain;
	
	public CaptchaCreator() {
		rand = new Random();
		tts = new TextToSpeech();
		adultM = new VoiceType("cmu-rms-hsmm", "adult_male");
		adultF = new VoiceType("dfki-poppy-hsmm", "adult_female");
		answer = "";
		listenFor = "";
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String expandNum(int rand, String text1) {
		switch(rand) {
			case 0:
				text1 = text1 + ". zero";
				break;
			case 1:
				text1 = text1 + ". one";
				break;
			case 2:
				text1 = text1 + ". two";
				break;
			case 3:
				text1 = text1 + ". three";
				break;
			case 4:
				text1 = text1 + ". four";
				break;
			case 5:
				text1 = text1 + ". five";
				break;
			case 6:
				text1 = text1 + ". six";
				break;
			case 7:
				text1 = text1 + ". seven";
				break;
			case 8:
				text1 = text1 + ". eight";
				break;
			case 9:
				text1 = text1 + ". nine";
				break;
			default:
				break;
			}
		return text1;
	}
	
	public void captchaGen() {
		try {
			answer = "";
			String text1 = "";
			String text2 = "";
			String name1 = "";
			String name2 = "";
			VoiceType voice1;
			VoiceType voice2;
			
			if(rand.nextBoolean() == true) {
				voice1 = adultM;
				voice2 = adultF;
				listenFor = "man";
				bgGain = 0.6;
			}
			else {
				voice1 = adultF;
				voice2 = adultM;
				listenFor = "woman";
				bgGain = 1.0;
			}
			
			for(int i = 0; i < 6; i++) {
				int ans = rand.nextInt(10);
				int ans2 = rand.nextInt(10);
				answer = answer + ans;
				name1 = name1 + ans;
				name2 = name2 + ans2;
				text1 = expandNum(ans, text1);
				text2 = expandNum(ans2, text2);
			}
			
			String file1 = tts.generateWav(text1, voice1);
			String file2 = tts.generateWav(text2, voice2);
			
			
			AudioFileMixer.mixTwoFiles(file1, 1.0, file2, bgGain, "CAPTCHAFile\\captcha" + name1+"_"+name2+".wav");
		} catch(UnsupportedAudioFileException e) {
			System.err.println("oops");
		} catch(IOException e) {
			System.err.println("oops");
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
		
	
	
	

}
