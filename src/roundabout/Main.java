package roundabout;

public class Main {
	/*
	 * パラメータ
	 */
	public static final int L = 10; // 正方格子の一辺の数
	public static final int m = 5;  // 道路サイト数
	public static final int N = 20; // 車の数(正方格子モデルでは一定)

	public static void main(String[] args) {

		// ★正方格子の宣言と初期化
		Cell[][] cells = new Cell[L][L];

		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				// TODO: mをprivateにする必要はないかもしれない。
				cells[i][j] = new Cell(m);
			}
		}

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


		// ★車の宣言と初期化
		Car[] cars = new Car[N];

		for (int i = 0; i < N; i++) {
			cars[i] = new Car();
		}

		// 車構造体のアドレスをCellに教える(C言語的言い回し)
		// TODO: もっと上手い方法は無いのか
		Cell.carList = cars;



	}

}
