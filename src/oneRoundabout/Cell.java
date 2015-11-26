package oneRoundabout;

import java.util.List;

/*
 * 印南論文 第4章 ラウンドアバウト交差点ルールの実装
 * @author T. Miyazaki
 *
 */

/**
 * 単位セル クラス
 * アサーションを有効にすること！！
 *
 * @author T. Miyazaki
 *
 */
public class Cell {

	// 車情報のリストへのアクセス
	public static List<Car> carList;

	// モデルインスタンスへのアクセス
	public static OneRoundaboutModel model;

	// 道路サイトの数 (Viewクラスから参照するためpublicに変更)
	@Deprecated public int m;
	// 自分のセルがどこの位置にあるかを知っている必要がある。
	private int cellI, cellJ;

	// 上下左右に隣接するCellへの参照
	// (circular doubly linked matrix (not a list))
	private Cell left, top, right, bottom;

	// サイトにおける車の存在を格納する変数
	public  int[][] mu, mu_new;
	private int[][] mu1, mu2;

	// サイトにおける車番を格納する変数
	public  int[][] num, num_new;
	private int[][] num1, num2;

	// サイトにおける車の移動方向を格納する変数
	public int[] a;


	// swapフラグ
	private boolean swapFlag = false;


	/**
	 * 単位セルを初期化します。
	 *
	 * @param i 横方向のインデックス(→に行くほど大)
	 * @param j 縦方向のインデックス(↓に行くほど大)
	 * @param m 道路サイト長
	 */
	public Cell(int i, int j, int m) {
		this.cellI = i;
		this.cellJ = j;

		assert m >= 2; // mが1以下だと道路サイトの更新ルールが適合しない
		this.m = m;

		// 配列の確保
		mu1 = new int[4][m + 1];
		mu2 = new int[4][m + 1];
		num1 = new int[4][m + 1];
		num2 = new int[4][m + 1];
		a = new int[4];

		swapBuffer();
	}


	/**
	 * 隣接するセルを設定します。
	 *
	 * @param left   左(r-x)に隣接するセル
	 * @param top    上(r-y)に隣接するセル
	 * @param right  右(r+x)に隣接するセル
	 * @param bottom 下(r+y)に隣接するセル
	 */
	public void setNeighbors(Cell left, Cell top, Cell right, Cell bottom) {
		this.left = left;		// r-x
		this.top = top;			// r-y
		this.right = right;		// r+x
		this.bottom = bottom;	// r+y
	}


	/**
	 * 単位セルの時間発展(アップデート)を行ないます。
	 *
	 * @return 動いた車の台数
	 */
	public int updateCell() {
		// 変数 a の反映
		updateA();
		//交差点サイトのアップデート
		updateRoundabouts();
		// 道路サイトのアップデート
		updateRoads();

		return countMoved();
	}

	/**
	 * バッファをスワップします。
	 * 全てのセルをアップデートした後に呼びます。
	 */
	public void swapBuffer() {
		if (swapFlag) {
			mu     = mu1;
			mu_new = mu2;
			num     = num1;
			num_new = num2;
		} else {
			mu     = mu2;
			mu_new = mu1;
			num     = num2;
			num_new = num1;
		}
		swapFlag = !swapFlag;
	}


	/**
	 * アップデートの前後で動いた車の台数を数えて返します。
	 *
	 * @return 動いた車の台数
	 */
	private int countMoved() {
		int c = 0;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < m + 1; j++) {
				c += (num[i][j] != num_new[i][j] ? 1 : 0);
			}
		}

		return c;
	}


	/**
	 * 指定されたサイトに車を発生させようと試みます。
	 *
	 * @param alpha 交差点番号
	 * @param beta  道路サイト番号
	 * @param n     セットする車番
	 * @return 車をセットできた場合 true
	 */
	@Deprecated
	public boolean spawnCar(int alpha, int beta, int n) {
		// 既に別の車がいれば失敗
		if (mu[alpha][beta] == 1)
			return false;

		// そうでなければ、車番をセットする
		mu[alpha][beta] = 1;
		num[alpha][beta] = n;
		return true;
	}

	/**
	 * 有効なサイトか？(車を置けない境界条件が考慮される)
	 *
	 * @param alpha 交差点番号
	 * @param beta 道路サイト番号
	 */
	public boolean isValidSite(int alpha, int beta) {
		int L = model.L;

		/*
		 * 車を置けないサイトだったらfalse
		 */
		return !(
			// 左端のセルの縁から伸びたサイト上だったらfalse
			(cellI == 0 && alpha == 0 && beta != 0)
			// 右端のセルの縁から伸びたサイト上だったらfalse
		|| (cellI == L-1 && alpha == 2 && beta != 0)
			// 上端のセルの縁から伸びたサイト上だったらfalse
		|| (cellJ == 0 && alpha == 1 && beta != 0)
			// 下端のセルの縁から伸びたサイト上だったらfalse
		|| (cellJ == L-1 && alpha == 3 && beta != 0));
	}


	/**
	 * 交差点サイトにいる車が動ける条件 M を返します。
	 *
	 * @param  alpha 交差点番号
	 * @return 動ける場合1、動けない場合0
	 */
	private int toMove(int alpha) {

		int u   = alpha;			// α
		int up  = (alpha + 1) % 4;	// α+
		int upp = (alpha + 2) % 4;	// α++
		int um  = (alpha + 3) % 4;	// α-

		// 以下の条件は全て排反事象なので、返り値は必ず0または1になるはず。

				// 自車が交差点を回ろうとする先に車がいない場合
		int k = mu[u][0] * (1-a[u]) * (1-mu[up][0])
				// 自車が道路サイトに抜けようとする先に車がいない場合
		      + mu[u][0] * a[u] * (1-mu[u][1])
		      	// 1台前の車が交差点を周ろうとし、その前方に車がいない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * (1-a[up]) * (1-mu[upp][0])
		      	// 1台前の車が道路に抜けようとし、その先に車がいない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * a[up] * (1-mu[up][1])
		      	// 2台前の車が交差点を回ろうとし、その前方に車がいない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * (1-a[up]) * mu[upp][0] * (1-a[upp]) * (1-mu[um][0])
		      	// 2台前の車が道路に抜けようとし、その先に車がいない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * (1-a[up]) * mu[upp][0] * a[upp] * (1-mu[upp][1])
		      	// 3台前(=真後ろ)の車も交差点を回ろうとし、全ての車が交差点を回る場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * (1-a[up]) * mu[upp][0] * (1-a[upp]) * mu[um][0] * (1-a[um])
		      	// 3台前(=真後ろ)の車が道路に抜けようとし、その先に車がいない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * (1-a[up]) * mu[upp][0] * (1-a[upp]) * mu[um][0] * a[um] * (1-mu[um][1]);

		assert (k == 0 || k == 1);

		return k;
	}

	/**
	 * 交差点サイトにいる車が動けない条件 S を返します。
	 *
	 * @param  alpha 交差点番号
	 * @return 動けない場合1、動ける場合0
	 */
	private int toStop(int alpha) {

		int u   = alpha;			// α
		int up  = (alpha + 1) % 4;	// α+
		int upp = (alpha + 2) % 4;	// α++
		int um  = (alpha + 3) % 4;	// α-

		// 以下の条件は全て排反事象なので、返り値は必ず0または1になるはず。

				// 自車が道路サイトに抜けようとする先に別の車がいる場合
		int k = mu[u][0] * a[u] * mu[u][1]
				// 1台前の車が道路サイトに抜けようとし、その先に別の車がいて動けない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * a[up] * mu[up][1]
		    	// 2台前の車が道路サイトに抜けようとし、その先に別の車がいて動けない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * (1-a[up]) * mu[upp][0] * a[upp] * mu[upp][1]
		    	// 3台前(=真後ろ)の車が道路サイトに抜けようとし、その先に別の車がいて動けない場合
		      + mu[u][0] * (1-a[u]) * mu[up][0] * (1-a[up]) * mu[upp][0] * (1-a[upp]) * mu[um][0] * a[um] * mu[um][1];

		assert (k == 0 || k == 1);

		return k;
	}


	/*
	 * アップデートルールについて
	 *
	 * 全てのサイトは、車がいるかいないかの2状態を持つ(変数 mu)。
	 * 車がいる場合、車の番号が入っている(変数 num)。
	 * 車がいない場合は、車の番号は 0 である。
	 * １ステップのアップデートで、車が出たセルに別の車が入ったり、
	 * 一台の車が２サイト以上動いたりはしない。
	 *
	 * 次の時刻t+1のサイトxの状態は、車の移動がなかった場合、そのまま、
	 * 車の移動があった場合、その変化後の状態になる。
	 *
	 * 全てのサイトは、その隣接するサイトの状態のみによって
	 * 次の状態が決定される。
	 *
	 */


	/**
	 * 交差点サイトをアップデートします。
	 *
	 * <p>次の時刻に、交差点サイトに車がいるのは、<br>
	 * 1) 交差点サイトにいる車が動けなかった場合<br>
	 * 2) 後ろの交差点サイトから車が進入してくる場合<br>
	 * 3) 道路サイトから車が進入してくる場合<br>
	 * のいずれかが成り立つ時となります。
	 *
	 */
	private void updateRoundabouts() {
		// 4箇所ある交差点を更新する。
		for (int u = 0; u < 4; u++) {

			int um  = (u + 3) % 4;	// α-

			// 交差点サイトに接続している、
			// 隣接セルの道路サイトの状態
			int mu_in = 0, num_in = 0;

			// TODO: 隣接セルの配列化と↓の最適化
			// TODO: [Car]がヌルポだったら参照しない(非周期境界のとき)
			switch (u) {
			case 0:
				mu_in  = bottom.mu[1][m];
				num_in = bottom.num[1][m];
				break;
			case 1:
				mu_in  = left.mu[2][m];
				num_in = left.num[2][m];
				break;
			case 2:
				mu_in  = top.mu[3][m];
				num_in = top.num[3][m];
				break;
			case 3:
				mu_in  = right.mu[0][m];
				num_in = right.num[0][m];
				break;
			}

			mu_new[u][0] =
				// ■後ろの交差点サイトから車が進入してくる場合
				// <=> 後ろの車が動ける条件が真で、それが後ろの車が道路サイトに抜けることによるものではない場合
				// 注：第2項が真になる時は常に第1項も真になっている(論理包含の関係)ので、この式が負になることはない。
				  toMove(um) - (mu[um][0] * a[um] * (1-mu[um][1]))
				// ■道路サイトから車が進入してくる場合 (交差点内の車が交差点を回るときはそちらが優先される)
				// ・進入先の交差点の1つ後ろの交差点サイトに車がいない場合
				// 進入先のサイトに車がいない場合(第1項)／進入先のサイトに車がいるが、動ける場合(第2項)
				+ mu_in * (1-mu[um][0]) * ((1-mu[u][0]) + toMove(u))
				// ・進入先の交差点の1つ後ろの交差点サイトに車がいるが、道路に抜ける場合
				// 進入先のサイトに車がいない場合(第1項)／進入先のサイトに車がいるが、動ける場合(第2項)
				+ mu_in * mu[um][0] * a[um] * ((1-mu[u][0]) + toMove(u))
				// ■交差点サイトにいる車が動けない場合
				+ toStop(u);

			// 車のインデックスの更新
			// 排反事象である上記の各条件の式に、そのサイトにおける車の車番を乗じることで求められる。
			// いずれの条件も満たされなければ0になる。これは車がいないことを示す。
			num_new[u][0] =
				  num[um][0] * (toMove(um) - (mu[um][0] * a[um] * (1-mu[um][1])))
				+ num_in * (mu_in * (1-mu[um][0]) * ((1-mu[u][0]) + toMove(u)))
				+ num_in * (mu_in * mu[um][0] * a[um] * ((1-mu[u][0]) + toMove(u)))
				+ num[u][0] * toStop(u);
		}
	}

	/*
	 * 道路サイトをアップデートします。
	 */
	private void updateRoads() {

		// 4つの交差点番号についてループを回す
		for (int u = 0; u < 4; u++) {

			// 交差点サイトに接続している、
			// 隣接セルの道路サイトの状態
			Cell out = null;
			int out_a = 0;

			// TODO: 隣接セルの配列化と↓の最適化
			// TODO: [Car]がヌルポだったら参照しない(非周期境界のとき)
			switch (u) {
			case 0:
				out = left;
				out_a = 3;
				break;
			case 1:
				out = top;
				out_a = 0;
				break;
			case 2:
				out = right;
				out_a = 1;
				break;
			case 3:
				out = bottom;
				out_a = 2;
				break;
			}

			// 進入する交差点のひとつ手前の交差点番号
			int out_am = (out_a + 3) % 4;

			/*
			 * 道路の入口となる道路サイトのアップデート
			 */
			mu_new[u][1] =
					// 交差点にいる車がこの道路サイトへ抜けてきた場合
					mu[u][0] * a[u] * (1-mu[u][1])
					// 前に車がいて動けない場合
					+ mu[u][1] * mu[u][2];

			num_new[u][1] =
					  num[u][0] * (mu[u][0] * a[u] * (1-mu[u][1]))
					+ num[u][1] * (mu[u][1] * mu[u][2]);

			/*
			 * 道路の出口となる道路サイトのアップデート
			 */
			mu_new[u][m] =
					// ひとつ手前の道路サイトの車が移動する場合
					  mu[u][m-1] * (1-mu[u][m])
					// 進入する交差点サイトにいる車が動けない場合
					+ mu[u][m] * out.toStop(out_a)
					// 交差点を回る車が優先的に動く場合
					// <=> 進入する交差点のひとつ手前の交差点にいる車が動け、かつ道路に抜けない場合
					+ mu[u][m] * (out.toMove(out_am) - out.mu[out_am][0] * out.a[out_am] * (1-out.mu[out_am][1]));

			num_new[u][m] =
					  num[u][m-1] * (mu[u][m-1] * (1-mu[u][m]))
					+ num[u][m] * (mu[u][m] * out.toStop(out_a))
					+ num[u][m] * (mu[u][m] * (out.toMove(out_am) - out.mu[out_am][0] * out.a[out_am] * (1-out.mu[out_am][1])));

			/*
			 * 交差点の入り口と出口を除く道路サイトについてループを回す
			 */
			for (int i = 2; i <= m - 1; i++) {
				// 後ろの車が移動する場合(第1項)または前に車がいて動けない場合(第2項)
				mu_new[u][i] = (mu[u][i-1] * (1-mu[u][i])) + (mu[u][i] * mu[u][i+1]);

				num_new[u][i] = num[u][i-1] * (mu[u][i-1] * (1-mu[u][i]))
						+ num[u][i] * (mu[u][i] * mu[u][i+1]);
			}
		}
	}


	/*
	 * 変数 a の更新
	 */
	private void updateA() {
		for (int u = 0; u < 4; u++) {
			// 交差点(α,0)に車がいるとき (車がいないサイトについてはa[x]は参照されない)
			if (num[u][0] != 0) {
				Car car = carList.get(num[u][0]);
				// 交差点(i,j,α)を回るか、抜けるか
//				a[u] = car.alpha[cellI][cellJ][u];
				a[u] = car.alpha[u];
			}
		}
	}
}
