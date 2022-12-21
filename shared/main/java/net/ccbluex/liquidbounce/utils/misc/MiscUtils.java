/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.misc;

import net.ccbluex.liquidbounce.utils.MinecraftInstance;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public final class MiscUtils extends MinecraftInstance {

    public static void showErrorPopup(final String title, final String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void playSound(SoundType st, float volume) {
        new Thread(() -> {
            AudioInputStream as;
            try {
                as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(this.getClass().getResourceAsStream("/assets/minecraft/sounds/" + st.getName()))));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public enum SoundType {
        MUSIC("music.wav"),
        VICTORY("victory.wav");
        final String music;

        SoundType(String fileName) {
            this.music = fileName;
        }

        String getName() {
            return music;
        }
    }

    public static void showURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        }catch(final IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static File openFileChooser() {
        if (mc.isFullScreen())
            mc.toggleFullscreen();

        final JFileChooser fileChooser = new JFileChooser();
        final JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        final int action = fileChooser.showOpenDialog(frame);
        frame.dispose();

        return action == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }

    public static File saveFileChooser() {
        if (mc.isFullScreen())
            mc.toggleFullscreen();

        final JFileChooser fileChooser = new JFileChooser();
        final JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        final int action = fileChooser.showSaveDialog(frame);
        frame.dispose();

        return action == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }
}
