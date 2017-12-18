package main;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.signalproc.process.AudioFileMixer;

public class Main {

	public static void main(String[] args) throws Exception {
		CaptchaCreator c = new CaptchaCreator();
		c.charCaptchaGen();
		
		try {
//			AudioMix.play("adult_male0.wav", "adult_female0.wav", 5, -10);
			AudioMix.mixTwoFiles("adult_male0.wav", 0.4, "adult_female0.wav", 1.0, "captcha.wav");
			
		} catch(UnsupportedAudioFileException e) {
			System.err.println("oops");
		} catch(IOException e) {
			System.err.println("oops");
		}
	}

}
