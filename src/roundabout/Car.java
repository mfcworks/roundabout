package roundabout;

// 車の情報の格納
public class Car {
	// 車番(要るのか？)
	private int num;

	// 出発地、目的地
	private int[] origin, destination;

	// この車が座標[i][j]の交差点[α]で
	// 交差点を回る(0)か道路に抜ける(1)か
	public int alpha[][][];

	/**
	 * コンストラクタ
	 *
	 * 車を生成する。すなわち、出発地と目的地を
	 * 決めて道順を定める。
	 */
	public Car() {

		selectOrigin();
		selectDestination();
		selectRoute();

	}


	// 出発地を決める
	private void selectOrigin() {
		// [i][j][a][b]
		// セル座標[i][j]の交差点番号[a]の道路番号[b]
		origin = new int[4];

		/*
		 * 1. ランダムに位置pを決める。
		 * 2. 位置pに車がいなければ、
		 * 3.   pを出発地とする。
		 * 4.   cellsの位置pに車を置く。
		 * 5. 位置pに車がいれば1.に戻る
		 */
	}

	// 目的地を決める
	private void selectDestination() {
		// [i][j][a][b]
		// セル座標[i][j]の交差点番号[a]の道路番号[b]
		destination = new int[4];

		/*
		 * 1. ランダムに(あるいは制限された範囲から
		 *    ランダムに)位置pを決める。
		 * 2. 位置pを目的地とする。
		 */
	}

	// 道順を決める
	private void selectRoute() {
		// 場合によっては出発地と目的地の座標を
		// 反対車線側に移動する

		// 道順を決定する
		// alpha[][][] に格納する
	}

	// デストラクタ
	public void finalize() {

	}

}
