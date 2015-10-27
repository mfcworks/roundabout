package roundabout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoundaboutModel {
	// 正方格子の一辺の数
	private int L;
	// 道路リンクの長さ
	private int m;
	// 単位セルの配列
	private Cell[][] cells;
	// 車のデータ
	private List<Car> cars;

	public RoundaboutModel(int L, int m) {
		this.L = L;
		this.m = m;

		cells = new Cell[L][L];

		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				cells[i][j] = new Cell(i, j, m);
			}
		}


		cars = new ArrayList<Car>();
	}




	// 初期化
	public void initialize(int numCars) {
		Random r = new Random();
		int num = 0;
		while (num < numCars) {
			// ランダムなサイトを選ぶ
			int i = r.nextInt(L);
			int j = r.nextInt(L);
			int a = r.nextInt(m);
			int b = r.nextInt(m);

			if (cells[i][j].initCar(a, b, num)) {
				num++;
			}
		}
	}


	// アップデート
	public void update() {
		// 全てのセルをupdateしてから
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				cells[i][j].updateCell();
			}
		}
		// 全てのセルのバッファをスワップする
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				cells[i][j].swapBuffer();
			}
		}
	}


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
