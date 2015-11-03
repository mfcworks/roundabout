package roundabout;

import java.util.ArrayList;
import java.util.List;

/**
 * １交差点モデル（L = 1, m = 比較的長い）
 * @author T. Miyazaki
 *
 * 以前の版は一時的に以下に退避
 * https://gist.github.com/t-miyazaki/381d6af5a7819160988d
 *
 */
public class OneRoundaboutModel {

	private int m;			// 道路リンクの長さ
	private Cell cell;		// 単位セル
	private List<Car> cars;	// 車のデータ


	/**
	 * コンストラクタ：モデルを構築します。
	 *
	 * @param L 正方格子の一辺の数
	 * @param m 道路サイトの長さ
	 */
	public OneRoundaboutModel(int m) {
		this.m = m;

		cell = new Cell(0, 0, m);

		// セルの接続を設定する。今回は1つのセルで周期境界にする。
		cell.setNeighbors(cell, cell, cell, cell);


		/*
			// ★セル同士の結合の設定
			// システムを上空から見た場合、左上(top-left)を[0][0]、
			// 右下(right-bottom)を[L-1][L-1]の座標になるようにする。
			// インデックス[i][j]は、[左右方向][上下方向]の順で用いる。
			for (int i = 0; i < L; i++) {
				for (int j = 0; j < L; j++) {
					// [i][j]のleftは[i-1][j]
					int left   = i==0 ? L-1 : i-1;
					// [i][j]のtopは[i][j-1]
					int top    = j==0 ? L-1 : j-1;
					// [i][j]のrightは[i+1][j]
					int right  = i==L-1 ? 0 : i+1;
					// [i][j]のbottomは[i][j+1]
					int bottom = j==L-1 ? 0 : j+1;

					cells[i][j].setNeighbor(
							cells[left][j],
							cells[i][top],
							cells[right][j],
							cells[i][bottom]);
				}
			}
		 */

	}

	/**
	 * 初期化：初期状態を生成します。
	 *
	 * @param numCars 発生させる車の台数
	 */
	public void initialize(int numCars) {

		// 1セルの全サイト数 = 4 * (m + 1)
		// 車の数はそれより少ない必要がある。
		// ※ 正方格子系の場合は i * j * 4 * (m + 1) にする。
		assert (numCars <= 4 * (m + 1));

		cars = new ArrayList<Car>(numCars);

		// dummy car (empty)
		cars.add(0, new Car(0));

		// CellとCarでの相互参照ができるようにする
		Cell.carList = cars;
		Car.cell = cell;
		Car.m = this.m;

		// numCars 台の車を発生
		for (int i = 1; i <= numCars; i++) {
			cars.add(i, new Car(i));
		}
	}


	// アップデート
	public void update() {
		// (全ての)セルをupdateしてから
		cell.updateCell();
		// (全ての)セルのバッファをスワップする
		cell.swapBuffer();
	}

	// 境界条件の処理
	public void spwanAndDespawn() {
		// 周期境界でずっと回り続けるので、
		// とりあえずは実装しなくて良い。
	}


	private String mk(int val) {
		return (val == 1 ? "*" : "･");
	}

	// mが偶数のときのみ使える簡易テキストプロッタ
	public void textPlot() {
		if (m % 2 != 0) return;

		int half = m / 2;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < half; i++)
			sb.append(" ");

		String blank = sb.toString();

		// 上に出た縦の道路
		for (int i = 0; i < half; i++) {
			System.out.print(blank);
			System.out.print(mk(cell.mu[1][half-i]));
			System.out.println(mk(cell.mu[3][half+i+1]));
		}

		// 横の道路の上側
		for (int i = 0; i < half; i++)
			System.out.print(mk(cell.mu[2][half+1+i]));
		System.out.print(mk(cell.mu[1][0]));
		for (int i = 0; i < half + 1; i++)
			System.out.print(mk(cell.mu[2][i]));
		System.out.println();

		// 横の道路の下側
		for (int i = half; i >= 0; i--)
			System.out.print(mk(cell.mu[0][i]));
		System.out.print(mk(cell.mu[3][0]));
		for (int i = m; i > half; i--)
			System.out.print(mk(cell.mu[0][i]));
		System.out.println();

		// 下に出た縦の道路
		for (int i = 0; i < half; i++) {
			System.out.print(blank);
			System.out.print(mk(cell.mu[1][m-i]));
			System.out.println(mk(cell.mu[3][i+1]));
		}

		System.out.println();
	}



	// ----- test -----
	public static void main(String[] args) {
		int my_m = 20;
		int my_n = 20;

		OneRoundaboutView view = new OneRoundaboutView(600, 600);

		// モデルを作る
		OneRoundaboutModel model = new OneRoundaboutModel(my_m);

		// 初期状態の生成
		model.initialize(my_n);

		model.textPlot();
		System.out.println("update");
		model.update();
		model.textPlot();
		System.out.println("正常に完了しました");

		view.drawCell(model.cell);

	}

}
