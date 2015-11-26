package oneRoundabout;

import java.util.Random;

/**
 * 車の情報を扱うクラス（正方格子系バージョン）
 *
 * 周期的境界条件はない。
 *
 * @author T. Miyazaki
 *
 */
public class Car {

	// モデルインスタンスへの参照(基本的な情報はここから参照する)
	public static OneRoundaboutModel model;

	// セル(配列)への参照
	public static Cell[][] cells;

	// 車番
	public int num;

	// 出発地・目的地の座標
	private int[] origin, destination;

	// この車が座標[i][j]の交差点[α]で
	// 交差点を回る(0)か道路に抜ける(1)か
	public int alpha[][][];

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
	 * 出発地を決める：
	 *
	 * 少なくとも車が置けるサイトを見つける。
	 */
	private void selectOrigin() {
		// [i][j][a][b]
		// 交差点番号[a] 道路番号[b]
		origin = new int[4];

		Random r = new Random();
		/*
		 * 1. ランダムに位置pを決める。
		 * 2. 位置pに車がいなければ、
		 * 3.   pを出発地とする。
		 * 4.   cellsの位置pに車を置く。←あとでやる
		 * 5. 位置pに車がいれば1.に戻る
		 */

		int i, j, a, b;
		do {
			i = r.nextInt(model.L);
			j = r.nextInt(model.L);
			a = r.nextInt(4);
			b = r.nextInt(model.m + 1);
		} while (!cells[i][j].isValidSite(a, b) || cells[i][j].mu[a][b] == 1);

		// 出発地の座標を設定
		origin[0] = i;
		origin[1] = j;
		origin[2] = a;
		origin[3] = b;
	}

	/*
	 * 目的地を決める
	 */
	private void selectDestination() {
		// [i][j][a][b]
		// 交差点番号[a] 道路番号[b]
		destination = new int[4];

		Random r = new Random();
		/*
		 * 1. ランダムに(あるいは制限された範囲から
		 *    ランダムに)位置pを決める。
		 * 2. 位置pを目的地とする。
		 */

		int i, j, a, b;
		do {
			i = r.nextInt(model.L);
			j = r.nextInt(model.L);
			a = r.nextInt(4);
			b = r.nextInt(model.m + 1);
		} while (!cells[i][j].isValidSite(a, b));

		// 目的地の座標を設定
		destination[0] = i;
		destination[1] = j;
		destination[2] = a;
		destination[3] = b;
	}


	// 道順を決める
	private void selectRoute() {
		// 場合によっては出発地と目的地の座標を
		// 反対車線側に移動しておく
		/*
		 * ・もし出発地を変えたほうがいいなら、反対車線に車がいなければ
		 *  出発地を変更する。
		 * ・もし目的地を変えたほうがいいなら、目的地を変更する。
		 */

		// それぞれの交差点番号における移動方向
		alpha = new int[model.L][model.L][4];
		// 念のため通らない交差点には-1を入れておく。
		for (int i = 0; i < model.L; i++) {
			for (int j = 0; j < model.L; j++) {
				for (int a = 0; a < 4; a++) {
					alpha[i][j][a] = -1;
				}
			}
		}

		/*
		 * 出発地の交差点番号が0, 2のとき、車は左か右に動く。
		 * 出発地の交差点番号が1, 3のとき、車は上か下に動く。
		 *
		 * 交差点を回る(0)か道路に抜ける(1)かを設定する。
		 * つまり、交差点を抜けるサイトに1を設定する。
		 */

	}

	// デストラクタ
	@Override
	public void finalize() {

	}

}
