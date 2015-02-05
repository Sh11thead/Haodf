package com.shithead.haodfcrawler.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class CapchaShow extends JFrame {
	/**
	 * @author 阿伦
	 */
	private static final long serialVersionUID = 1L;

	private String capcha;

	private JLabel jLabel;
	private JTextField jTextField;
	private JButton jButton;
	private JLabel capchaLabel;

	public CapchaShow(byte[] pic) {
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(300, 250);
		this.getContentPane().setLayout(null);
		this.add(getJLabel(), null);
		this.add(getJTextField(), null);
		this.add(getJButton(), null);
		this.add(getCapchaLabel(pic));
		this.setTitle("请输入验证码");
		this.setVisible(true);
	}

	private JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			jLabel.setBounds(34, 90, 53, 18);
			jLabel.setText("Capcha:");
		}
		return jLabel;
	}

	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(96, 90, 160, 20);
		}
		return jTextField;
	}

	private JLabel getCapchaLabel(byte[] pic) {
		if (capchaLabel == null) {
			capchaLabel = new JLabel();
			capchaLabel.setBounds(40, 20, 300, 80);
			BufferedImage bi = null;
			try {
				bi = ImageIO.read(new ByteArrayInputStream(pic));
			} catch (IOException e) {
				e.printStackTrace();
			}
			ImageIcon ic = new ImageIcon(Toolkit.getDefaultToolkit().createImage(bi.getSource()));
			capchaLabel.setIcon(ic);
		}
		return capchaLabel;
	}

	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(103, 120, 71, 27);
			jButton.setText("OK");
			jButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					capcha = jTextField.getText();
					close();
				}
			});
		}
		return jButton;
	}

	protected void close() {
		this.setVisible(false);
	}

	public CapchaShow(String capcha, JLabel jLabel, JTextField jTextField, JButton jButton, JLabel capchaLabel)
			throws HeadlessException {
		super();
		this.capcha = capcha;
		this.jLabel = jLabel;
		this.jTextField = jTextField;
		this.jButton = jButton;
		this.capchaLabel = capchaLabel;
	}

	public String getCapcha() {
		while (StringUtils.isBlank(capcha)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return capcha;
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		CapchaShow w = new CapchaShow(FileUtils.readFileToByteArray(new File("F:/trafficViolation/统计信息/验证码/安徽.gif")));
		System.out.println(w.getCapcha());
	}
}
