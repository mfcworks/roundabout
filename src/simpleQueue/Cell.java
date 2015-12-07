package simpleQueue;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

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

	// 自分のセルがどこの位置にあるかを知っている必要がある。
	private int cellI, cellJ;


	/***** プロパティ *****/
	// 道路サイトの数
	public final int m;
	// 車線数
	public final int n;

	/***** 自己データ *****/
	// 交差点サイト(4サイト、交差点番号0～3)【車番を格納】
	public int[] roundabout;
	public int[] roundabout_new; // 更新用バッファ
	// 道路サイト【車番を格納】
	public Queue<Integer>[][] roadSites;
	public int[][] roadNums; // 現時刻で各道路サイトにいる車の数

	/***** 接続 *****/
	// 上下左右に隣接するCellへの参照
	// (circular doubly linked matrix (not a list))
	private Cell left, top, right, bottom;

	// サイトにおける車の存在を格納する変数
//	public  int[][] mu, mu_new;
//	private int[][] mu1, mu2;

	// サイトにおける車番を格納する変数
//	public  int[][] num, num_new;
//	private int[][] num1, num2;

	// サイトにおける車の移動方向を格納する変数
	public int[] a;


	// swapフラグ
//	private boolean swapFlag = false;


	// コンストラクタ
	/**
	 * 単位セルを初期化します。
	 *
	 * @param i 横方向のインデックス(→に行くほど大)
	 * @param j 縦方向のインデックス(↓に行くほど大)
	 * @param m 道路サイト長
	 * @param n 車線数
	 */
	@SuppressWarnings("unchecked")
	public Cell(int i, int j, int m, int n) {
		this.cellI = i;
		this.cellJ = j;

		assert m >= 2; // mが1以下だと道路サイトの更新ルールが適合しない
		this.m = m;

		assert n >= 1; // 車線数は1以上。
		this.n = n;

		// 配列の確保
		roundabout = new int[4];
		roundabout_new = new int[4];

		roadSites = (Queue<Integer>[][]) new ArrayDeque<?>[4][m];
		for (int u = 0; u < 4; u++) {
			for (int mm = 0; mm < m; mm++) {
				roadSites[u][mm] = new ArrayDeque<Integer>();
			}
		}
		roadNums = new int[4][m];

		a = new int[4];

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
	 * アップデートの前後で動いた車の台数を数えて返します。
	 *
	 * @return 動いた車の台数
	 */
	private int countMoved() {
		int c = 0;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < m + 1; j++) {
				//c += (num[i][j] != num_new[i][j] ? 1 : 0);
				// TODO 動いた車の台数を数える機能の実装
			}
		}

		return c;
	}


	/**
	 * 指定されたサイトに車を発生させようと試みます。
	 * TODO: この辺は全く別のやり方を考える！
	 *
	 * @param alpha 交差点番号
	 * @param beta  道路サイト番号
	 * @param n     セットする車番
	 * @return 車をセットできた場合 true
	 */
/*	public boolean spawnCar(int alpha, int beta, int n) {
		// 既に別の車がいれば失敗
		if (mu[alpha][beta] == 1)
			return false;

		// そうでなければ、車番をセットする
		mu[alpha][beta] = 1;
		num[alpha][beta] = n;
		return true;
	}*/


	/**
	 * 交差点サイトにいる車が動ける条件 M を返します。
	 *
	 * @param  alpha 交差点番号
	 * @return 動ける場合1、動けない場合0
	 */
	private int toMove(int alpha) {
		/*
		 * 交差点サイト mu[n][0] は ra[n] に置き換え。
		 * 道路サイト入り口 mu[n][1] は rd[n] に置き換え。
		 */

		int[] ra = new int[4]; // 交差点サイトに車がいるかいないか
		for (int i = 0; i < 4; i++) {
			ra[i] = (roundabout[i] == 0 ? 0 : 1);
		}

		int[] rd = new int[4];
		for (int i = 0; i < 4; i++) {
			// 道路サイト入り口がfullなら1, fullでなければ0
			rd[i] = (roadNums[i][0] == n ? 1 : 0);
		}

		int u   = alpha;			// α
		int up  = (alpha + 1) % 4;	// α+
		int upp = (alpha + 2) % 4;	// α++
		int um  = (alpha + 3) % 4;	// α-


				// 自車が交差点を回ろうとする先に車がいない場合
		int k = ra[u] * (1-a[u]) * (1-ra[up])
				// 自車が道路サイトに抜けようとする先のサイトがfullでない場合
		      + ra[u] * a[u] * (1-rd[u])
		      	// 1台前の車が交差点を周ろうとし、その前方に車がいない場合
		      + ra[u] * (1-a[u]) * ra[up] * (1-a[up]) * (1-ra[upp])
		      	// 1台前の車が道路に抜けようとし、その先のサイトがfullでない場合
		      + ra[u] * (1-a[u]) * ra[up] * a[up] * (1-rd[up])
		      	// 2台前の車が交差点を回ろうとし、その前方に車がいない場合
		      + ra[u] * (1-a[u]) * ra[up] * (1-a[up]) * ra[upp] * (1-a[upp]) * (1-ra[um])
		      	// 2台前の車が道路に抜けようとし、その先のサイトがfull出ない場合
		      + ra[u] * (1-a[u]) * ra[up] * (1-a[up]) * ra[upp] * a[upp] * (1-rd[upp])
		      	// 3台前(=真後ろ)の車も交差点を回ろうとし、全ての車が交差点を回る場合
		      + ra[u] * (1-a[u]) * ra[up] * (1-a[up]) * ra[upp] * (1-a[upp]) * ra[um] * (1-a[um])
		      	// 3台前(=真後ろ)の車が道路に抜けようとし、その先のサイトがfullでない場合
		      + ra[u] * (1-a[u]) * ra[up] * (1-a[up]) * ra[upp] * (1-a[upp]) * ra[um] * a[um] * (1-rd[um]);

		// 以上の条件は全て排反事象なので、返り値は必ず0または1になるはず。
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

		int[] ra = new int[4]; // 交差点サイトに車がいるかいないか
		for (int i = 0; i < 4; i++) {
			ra[i] = (roundabout[i] == 0 ? 0 : 1);
		}

		int[] rd = new int[4];
		for (int i = 0; i < 4; i++) {
			// 道路サイト入り口がfullなら1, fullでなければ0
			rd[i] = (roadNums[i][0] == n ? 1 : 0);
		}

		int u   = alpha;			// α
		int up  = (alpha + 1) % 4;	// α+
		int upp = (alpha + 2) % 4;	// α++
		int um  = (alpha + 3) % 4;	// α-


				// 自車が道路サイトに抜けようとする先に別の車がいる場合
		int k = ra[u] * a[u] * rd[u]
				// 1台前の車が道路サイトに抜けようとし、その先に別の車がいて動けない場合
		      + ra[u] * (1-a[u]) * ra[up] * a[up] * rd[up]
		    	// 2台前の車が道路サイトに抜けようとし、その先に別の車がいて動けない場合
		      + ra[u] * (1-a[u]) * ra[up] * (1-a[up]) * ra[upp] * a[upp] * rd[upp]
		    	// 3台前(=真後ろ)の車が道路サイトに抜けようとし、その先に別の車がいて動けない場合
		      + ra[u] * (1-a[u]) * ra[up] * (1-a[up]) * ra[upp] * (1-a[upp]) * ra[um] * a[um] * rd[um];

		// 以上の条件は全て排反事象なので、返り値は必ず0または1になるはず。
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

	// @苦肉の策
	// roundabout[n-1]の車がroundabout[n]に動けるかどうか
	private boolean movable(int n, int start) {

		// 全ての(この場合は4つの)サイトに車がいて、それぞれが
		// 交差点を回る場合、再帰呼び出しが無限ループしてしまう。
		// それを回避するため、nとstartが等しくなったときは
		// 自分自身が動けるかどうかを調べるために自分自身を調べる
		// という状況なので、trueを返す。(意味不)
		if (n == start)
			return true;

		// 自分の位置に車がいなければ後ろの車は入ってこれる。
		if (roundabout[n] == 0)
			return true;

		/* 以下、自分の位置に車がいる場合 */

		// 自分の車が道路へ抜けたいならば、前段の処理で道路がfullであった場合なので、自分の車が動けない。
		// よって後ろの車も動けない。
		if (a[n] == 1)
			return false;

		/* 以下、自分の車が交差点を回る場合 */

		// 前の車が動ければ追随できるためtrue。
		int np = (n + 1) % 4;

		// startが無意味な値の場合、再帰呼び出しでない。
		// 再帰呼び出しでない場合、startにnをセットする。
		// 再帰呼び出しの場合、startはそのまま渡す。
		return movable(np, (start < 0 ? n : start));
	}



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
		// 現時刻の交差点サイトにおける車番は roundabout[i] に格納されている。

		// 道路サイトへ抜ける処理
		for (int u = 0; u < 4; u++) {
			// 交差点にいる車が道路に抜けたくて、道路が空いている場合
			if (roundabout[u] != 0 && a[u] == 1 && roadNums[u][0] != n) {
				// 道路に抜ける
				roadSites[u][0].add(roundabout[u]);
				// 道路に抜けたので、今は車はいない
				roundabout[u] = 0;
			}
		}

		// 交差点を回る処理
		int[] temp = new int[4];

		for (int u = 0; u < 4; u++) {
			int um = (u + 3) % 4;
			// もし、後ろの車が「動ける」ならば、その車を動かす。
			if (movable(um, -1))
				temp[u] = roundabout[um];
			// 「動けない」ならば、車番は更新されない。
			else
				temp[u] = roundabout[u];
		}

		for (int i = 0; i < 4; i++)
			roundabout[i] = temp[i];


		// 最後に、
		// 接続された道路から車が入ってくる処理
		for (int u = 0; u < 4; u++) {

			Cell[] cells = {bottom, left, top, right};
			int up = (u + 1) % 4;

			// 道路が空いていて、入ってきたい車がいる場合
			if (roundabout[u] == 0 && cells[u].roadNums[up][m-1] != 0) {
				roundabout[u] = cells[u].roadSites[up][m-1].remove();
			}
		}


	}

	/*
	 * 道路サイトをアップデートします。
	 */
	private void updateRoads() {

		// 4つの交差点についてループを回す
		for (int u = 0; u < 4; u++) {

			// m個の道路サイトについて、m-1回ループを回す
			for (int i = 0; i < m - 1; i++) {
				// この道路サイトにいる車の数か、次の道路サイトの空きの数の小さい方だけ動ける。
				int moves = Math.min(roadNums[u][i], n - roadNums[u][i+1]);

				for (int j = 0; j < moves; j++) {
					roadSites[u][i+1].add(roadSites[u][i].remove());
				}
			}

		}

	}


	/*
	 * 変数 a の更新
	 */
	private void updateA() {
		for (int u = 0; u < 4; u++) {
			// 交差点(α,0)に車がいるとき (車がいないサイトについてはa[x]は参照されない)
			if (roundabout[u] != 0) {
				Car car = carList.get(roundabout[u]);
				// 交差点(i,j,α)を回るか、抜けるか
//				a[u] = car.alpha[cellI][cellJ][u];
				a[u] = car.alpha[u];
			}
		}
	}
}
