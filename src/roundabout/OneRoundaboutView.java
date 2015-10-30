package roundabout;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class OneRoundaboutView extends JPanel {

	private JFrame frame;
	private Cell cell;

	/**
	 * ウィンドウを作成します。
	 *
	 * @param winX 横幅のピクセル数
	 * @param winY 縦幅のピクセル数
	 */
	public OneRoundaboutView(int winX, int winY) {

		/*
		 * メインウィンドウ
		 */
		// 適当なタイトルでウィンドウを作成
		frame = new JFrame("OneRoundaboutModel Viewer");
		// 閉じるボタンで終了する
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// OSがデフォルトで提供する位置にウィンドウを表示する
		frame.setLocationByPlatform(true);
		// サイズ変更不可能にする
		frame.setResizable(false);
		// サイズを設定
		frame.setSize(winX, winY);

		/*
		 * 描画用パネル
		 */
//		this.setBackground(bg);
		// このJPanelを追加
		frame.add(this); // frame.getContentPane().add(this); でなくても良いみたい

		// メインウィンドウを表示する
		frame.setVisible(true);
	}


	/**
	 * 現在のCell(配列)の状態を描画します。
	 *
	 * @param cell
	 */
	public void drawCell(Cell cell) {
		this.cell = cell;

		this.repaint();
	}

	// this.repaint() から呼び出される
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawImpl(g);
	}

	// 実際に描画する処理
	private void drawImpl(Graphics g) {
		// 背景を 白色 で塗りつぶす
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.BLACK);

		// TODO: XXX: 描画処理を実装する
		for (int i = 0; i < 4; i++) {
			//for ...
		}
	}
}
