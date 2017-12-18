package main;

import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.signalproc.process.AudioFileMixer;

public class CaptchaCreator {
	private VoiceType adultM;
	VoiceType adultF;
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
	
	public void concat(String wavFile1, String wavFile2, String newname, boolean pre) {
		tts.appendWav(wavFile1, wavFile2, newname, pre);
	}
	
	public void genNum() {
		
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String expandNum(int rand) {
		String text = "";
		switch(rand) {
			case 0:
				text = ". zero... ";
				break;
			case 1:
				text = ". one... ";
				break;
			case 2:
				text = ". two... ";
				break;
			case 3:
				text = ". three... ";
				break;
			case 4:
				text = ". four... ";
				break;
			case 5:
				text = ". five... ";
				break;
			case 6:
				text = ". six... ";
				break;
			case 7:
				text = ". seven... ";
				break;
			case 8:
				text = ". eight... ";
				break;
			case 9:
				text = ". nine... ";
				break;
			default:
				break;
			}
		return text;
	}
	
	public void digitCaptchaGen() {
		try {
			answer = "";
			String name1 = "";
			String name2 = "";
			int seq = 0;
			int seq2 = 1;
			VoiceType voice1;
			VoiceType voice2;
			
			voice1 = adultF;
			voice2 = adultM;
			listenFor = "man";
			bgGain = 0.5;
			
			for(int i = 0; i < 6; i++) {
				int ans = rand.nextInt(10);
				int ans2 = rand.nextInt(10);
				answer = answer + ans;
				name1 = name1 + ans;
				name2 = name2 + ans2;
				if(i==0) {
					tts.addPreSilence(tts.generateWav(expandNum(ans), voice1), 1, "digits1_pre");
					tts.addPostSilence("digits1_pre.wav", 1, "digits1" + seq);
					tts.addPreSilence(tts.generateWav(expandNum(ans2), voice2), 1, "digits2_pre");
					tts.addPostSilence("digits2_pre.wav", 1, "digits2" + seq);
				}
				else {
					seq = (seq == 0) ? 1 : 0;
					seq2 = (seq2 == 0) ? 1 : 0;
					tts.addPostSilence(tts.generateWav(expandNum(ans), voice1), 1, "nextnum");
					tts.appendWav("digits1" + seq2 + ".wav","nextnum.wav","digits1" + seq, false);
					tts.addPostSilence(tts.generateWav(expandNum(ans2), voice2), 1, "nextnum2");
					tts.appendWav("digits2" + seq2 + ".wav","nextnum2.wav","digits2" + seq, false);
				}
			}

			AudioFileMixer.mixTwoFiles("digits1" + seq + ".wav", 0.5, "digits2" + seq + ".wav", 
					bgGain, "dig" + name1+"f_"+name2+"m.wav");
		} catch(UnsupportedAudioFileException e) {
			System.err.println("oops");
		} catch(IOException e) {
			System.err.println("io");
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void charCaptchaGen() {
		try {
			answer = "";
			String name1 = "";
			String name2 = "";
			int seq = 0;
			int seq2 = 1;
			VoiceType voice1;
			VoiceType voice2;
			
			voice1 = adultF;
			voice2 = adultM;
			listenFor = "man";
			bgGain = 0.5;
			
			for(int i = 0; i < 6; i++) {
				char ans = (char) (rand.nextInt(26) + 'a');
				char ans2 = (char) (rand.nextInt(26) + 'a');
				answer = answer + ans;
				name1 = name1 + ans;
				name2 = name2 + ans2;
				if(i==0) {
					tts.addPreSilence(tts.generateWav(Character.toString(ans), voice1), 5, "chars1_pre");
					tts.addPostSilence("chars1_pre.wav", 5, "chars1" + seq);
					tts.addPreSilence(tts.generateWav(Character.toString(ans2), voice2), 5, "chars2_pre");
					tts.addPostSilence("chars2_pre.wav", 5, "chars2" + seq);
				}
				else {
					seq = (seq == 0) ? 1 : 0;
					seq2 = (seq2 == 0) ? 1 : 0;
					tts.addPostSilence(tts.generateWav(Character.toString(ans), voice1), 5, "nextchar");
					tts.appendWav("chars1" + seq2 + ".wav","nextchar.wav","chars1" + seq, false);
					tts.addPostSilence(tts.generateWav(Character.toString(ans2), voice2), 5, "nextchar2");
					tts.appendWav("chars2" + seq2 + ".wav","nextchar2.wav","chars2" + seq, false);
				}
			}

			AudioFileMixer.mixTwoFiles("chars1" + seq + ".wav", 0.5, "chars2" + seq + ".wav", 
					bgGain, "char" + name1+"f_"+name2+"m.wav");
		} catch(UnsupportedAudioFileException e) {
			System.err.println("oops");
		} catch(IOException e) {
			System.err.println("io");
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public String combineWords(String word1, String word2) {
		try {
			AudioFileMixer.mixTwoFiles(tts.generateWav(word1, adultF), 0.5, tts.generateWav(word2, adultM), 
					0.5, "f_"+word1+"_m_"+word2+".wav");
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "f_"+word1+"_m_"+word2+".wav";
		
	}
	
	
	
	

}
