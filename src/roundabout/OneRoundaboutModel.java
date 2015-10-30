package roundabout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	}

	/**
	 * 初期化：初期状態を生成します。
	 *
	 * @param numCars 発生させる車の台数
	 */
	public void initialize(int numCars) {

		cars = new ArrayList<Car>(numCars);

		// CellとCarでの相互参照ができるようにする
		Cell.carList = cars;
		Car.cell = cell;

		Random r = new Random();
		int num = 0;
		while (num < numCars) {
			// ランダムなサイトを選ぶ
			int a = r.nextInt(m);
			int b = r.nextInt(m);

			if (cells[i][j].initCar(a, b, num)) {
				num++;
			}
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

	}


	// ----- test -----
	public static void main(String[] args) {
		int my_m = 4;
		int my_n = 10;

		// モデルを作る
		OneRoundaboutModel model = new OneRoundaboutModel(my_m);

		// 初期状態の生成
		model.initialize(my_n);
	}

}
