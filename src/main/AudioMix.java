package main;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.util.data.BufferedDoubleDataSource;
import marytts.util.data.audio.AudioDoubleDataSource;
import marytts.util.data.audio.DDSAudioInputStream;

public class AudioMix {
	
	public static void mixTwoFiles(String inputFile1, double mixAmount1, String inputFile2, double mixAmount2, String outputFile)
			throws UnsupportedAudioFileException, IOException {
		AudioInputStream inputAudio1 = AudioSystem.getAudioInputStream(new File(inputFile1));
		int samplingRate1 = (int) inputAudio1.getFormat().getSampleRate();
		System.out.println(samplingRate1);
		System.out.println(inputAudio1.getFormat().hashCode());
		AudioDoubleDataSource signal1 = new AudioDoubleDataSource(inputAudio1);
		double[] x1 = signal1.getAllData();

		AudioInputStream inputAudio2 = AudioSystem.getAudioInputStream(new File(inputFile2));
		int samplingRate2 = (int) inputAudio2.getFormat().getSampleRate();
		System.out.println(samplingRate2);
		System.out.println(inputAudio2.getFormat().hashCode());
		AudioDoubleDataSource signal2 = new AudioDoubleDataSource(inputAudio2);
		double[] x2 = signal2.getAllData();

		if (samplingRate1 != samplingRate2)
			System.out.println("Error! Sampling rates must be identical for mixing...");
		else {
			int i;
			double[] x3 = new double[Math.max(x1.length, x2.length)];

			if (x1.length > x2.length) {
				for (i = 0; i < x2.length; i++)
					x3[i] = mixAmount1 * x1[i] + mixAmount2 * x2[i];
				for (i = x2.length; i < x3.length; i++)
					x3[i] = mixAmount1 * x1[i];
			} else {
				for (i = 0; i < x1.length; i++)
					x3[i] = mixAmount1 * x1[i] + mixAmount2 * x2[i];
				for (i = x1.length; i < x3.length; i++)
					x3[i] = mixAmount2 * x2[i];
			}

			DDSAudioInputStream outputAudio = new DDSAudioInputStream(new BufferedDoubleDataSource(x3), inputAudio1.getFormat());
			AudioSystem.write(outputAudio, AudioFileFormat.Type.WAVE, new File(outputFile));
		}
	}

    public static Clip play(String filename, String filename2, float gain1, float gain2) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.setFramePosition(0);
        
        AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(new File(filename2));
        Clip clip2 = AudioSystem.getClip();
        clip2.open(audioInputStream2);
        clip2.setFramePosition(0);

        // values have min/max values, for now don't check for outOfBounds values
        FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain1);
        
        FloatControl gainControl2 = (FloatControl)clip2.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl2.setValue(gain2);

        clip.start();
        clip2.start();
        Thread.sleep(5500);
        return clip;
    }
}
