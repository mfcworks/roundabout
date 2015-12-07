package oneRoundabout;

import java.util.Random;

/**
 * 車の情報を扱うクラス（単一交差点バージョン）
 *
 * 単一セルの周期境界とする。
 * 車は常に直進するものとする。
 *
 * @author T. Miyazaki
 *
 */
public class Car {

	// セル(配列)への参照
	public static Cell cell;

	public static int m;

	// 車番
	public int num;

	// 出発地・目的地の座標
	private int[] origin, destination;

	// この車が座標[i][j]の交差点[α]で
	// 交差点を回る(0)か道路に抜ける(1)か
	public int alpha[];

	/**
	 * コンストラクタ
	 *
	 * 車を生成する。すなわち、出発地と目的地を
	 * 決めて道順を定める。
	 *
	 * @param num 車番
	 */
	public Car(int num) {
		this.num = num;

		// dummy car (represents empty)
		if (num == 0) return;

		// 出発地を決める
		selectOrigin();
		// 目的地を決める(今回は設定しない)
		selectDestination();
		// 道順を決める(今回は永遠に直進する)
		selectRoute();
	}


	public boolean isUsed() {
		return (num != 0);
	}


	/*
	 * 出発地を決める
	 */
	private void selectOrigin() {
		// [i][j][a][b]
		// 交差点番号[a] 道路番号[b]
		origin = new int[2];

		Random r = new Random();
		/*
		 * 1. ランダムに位置pを決める。
		 * 2. 位置pに車がいなければ、
		 * 3.   pを出発地とする。
		 * 4.   cellsの位置pに車を置く。
		 * 5. 位置pに車がいれば1.に戻る
		 */

		int i, j;
		do {
			i = r.nextInt(4);
			j = r.nextInt(m + 1);
		} while (!cell.spawnCar(i, j, num));

		origin[0] = i; // 交差点番号をセット
		origin[1] = j; // 道路番号をセット
	}

	// 目的地を決める
	private void selectDestination() {
		// [i][j][a][b]
		// 交差点番号[a] 道路番号[b]
		destination = new int[2];

		/*
		 * 1. ランダムに(あるいは制限された範囲から
		 *    ランダムに)位置pを決める。
		 * 2. 位置pを目的地とする。
		 */

		// 今回は動き続けるので目的地は設定しない。

	}

	// 道順を決める
	private void selectRoute() {
		// 場合によっては出発地と目的地の座標を
		// 反対車線側に移動しておく

		// それぞれの交差点番号における移動方向
		alpha = new int[4];

		/*
		 * 出発地の交差点番号が0, 2のとき、車は左か右に動く。
		 * 出発地の交差点番号が1, 3のとき、車は上か下に動く。
		 *
		 * 交差点を回る(0)か道路に抜ける(1)かを設定する。
		 * つまり、交差点を抜けるサイトに1を設定する。
		 * 車は直進するので、常に同じサイトから抜ける。
		 * よって、出発地の交差点番号と同じ交差点番号で
		 * 常に抜ければ良い。(それ以外の交差点では0である。)
		 */
		alpha[origin[0]] = 1;
	}

	// デストラクタ
	@Override
	public void finalize() {

	}

}
