package oneRoundabout;

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

		if (cell != null) drawImpl(g);
	}

	// 中心(x, y), 半径rの閉じた円を描画
	private static void circle(Graphics g, int x, int y, int r) {
		g.fillOval(x - r, y - r, 2 * r, 2 * r);
	}

	// 実際に描画する処理
	private void drawImpl(Graphics g) {
		// 背景を 白色 で塗りつぶす
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.BLUE);


		int offsetX = 10, offsetY = 10; // 描画起点位置
		int unit = 20; // 単位要素のサイズ

		int m = cell.m;
		assert m % 2 == 0; // mは偶数を仮定

		int mh = m / 2; // m half

		/*
		 * 枠線を描画
		 */
		g.setColor(Color.BLACK);
		for (int i = 0; i < 3; i++) {
			// 縦方向の車線の縦の長い線
			int x = offsetX + unit * (mh + i);
			int y = offsetY + unit * (m + 2);
			g.drawLine(x, offsetY, x, y);
			// 横方向の車線の横の長い線
			x = offsetX + unit * (m + 2);
			y = offsetY + unit * (mh + i);
			g.drawLine(offsetX, y, x, y);
		}
		for (int i = 0; i < m + 3; i++) {
			// 縦方向の車線の横の短い線
			int x = offsetX + unit * mh;
			int y = offsetY + unit * i;
			g.drawLine(x, y, x + unit * 2, y);
			// 横方向の車線の縦の短い線
			x = offsetX + unit * i;
			y = offsetY + unit * mh;
			g.drawLine(x, y, x, y + unit * 2);
		}

		/*
		 * 車を描画
		 */
		String[] arrows = {"←", "↑", "→", "↓"};
		int[] x = {offsetX + unit * mh, offsetX + unit * mh,
				offsetX + unit * (mh + 1), offsetX + unit * (mh + 1)};
		int[] y = {offsetY + unit * (mh + 1), offsetY + unit * mh,
				offsetY + unit * mh, offsetY + unit * (mh + 1)};
		for (int i = 0; i < m + 1; i++) {
			for (int a = 0; a < 4; a++) {
				if (cell.mu[a][i] == 1) {
					int e = 2; // サイズ調整の縁
					g.setColor(Color.CYAN);
					g.fillOval(x[a] + e, y[a] + e, unit - 2*e, unit - 2*e);
					g.setColor(Color.BLACK);
					String snum = String.valueOf(cell.num[a][i]);
					g.drawString((snum.length() == 1 ? " " + snum : snum), x[a]+3, y[a]+15);
				} else if (i != 0) {
					g.setColor(Color.BLACK);
					g.drawString(arrows[a], x[a]+5, y[a]+15);
				}
				switch (a) {
				case 0: x[0] -= unit; break;
				case 1: y[1] -= unit; break;
				case 2: x[2] += unit; break;
				case 3: y[3] += unit; break;
				}
			}

			if (i != mh) continue;
			//切り替え
			x[0] = offsetX + unit * (m + 1);
			y[1] = offsetY + unit * (m + 1);
			x[2] = offsetX;
			y[3] = offsetY;
		}
	}
}
