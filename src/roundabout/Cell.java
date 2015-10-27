package roundabout;

import java.util.List;

/**
 * 印南論文 第4章 ラウンドアバウト交差点ルールの実装
 * @author T. Miyazaki
 *
 */
public class Cell {

	// 車情報のリストへのアクセス
	public static List<Car> carList;

	private int m;

	// 自分のセルがどこの位置にあるかを知っている必要がある。
	private int cellI, cellJ;

	//交差点・道路サイトに車番を格納
	private int[][] sites1, sites2;

	// これをpublicにしておく
	public int[][] sites, sites_new;

	private boolean swapFlag = false;

	// 各交差点の接続先のCell
	// (circular doubly linked matrix (not a list))
	private Cell left, top, right, bottom;

	// 接続先のセルを設定する
	public void setNeighbor(Cell left, Cell top, Cell right, Cell bottom) {
		this.left = left;		// r-x
		this.top = top;			// r-y
		this.right = right;		// r+x
		this.bottom = bottom;	// r+y
	}

	// バッファをスワップ
	public void swapBuffer() {
		if (swapFlag) {
			sites = sites1;
			sites_new = sites2;
		} else {
			sites = sites2;
			sites_new = sites1;
		}
		swapFlag = !swapFlag;
	}




	// 唯一のコンストラクタ
	public Cell(int i, int j, int m) {
		this.cellI = i;
		this.cellJ = j;
		this.m = m;

		sites1 = new int[4][m + 1];
		sites2 = new int[4][m + 1];
		swapBuffer();

	}


	// μ_(α,β)^t を返します。
	public boolean mu(int alpha, int beta) {
		return (sites[alpha][beta] != 0);
	}

	// n_(α,β)^t を返します。
	public int num(int alpha, int beta) {
		return sites[alpha][beta];
	}


	// a_(α)^t を返します。
	// 交差点番号αにいる車がその交差点を回る(false)か、
	// その交差点から道路サイトに抜ける(true)かを返す。
	/*
	 * ※前提プログラムでは、車の走行情報を変数roadに格納しておき、
	 * 1ステップ発展するごとに変数aに情報を書き換えているようである。
	 * 本プログラムでは参照時に車情報リストからfetchするようにする。
	 */
	public boolean aa(int alpha) throws ArrayIndexOutOfBoundsException {

		// 車番
		int num = sites[alpha][0];
		Car car = carList.get(num);

		if (car.num != num) {
			// この例外がスローされたということはバグってるということ
			// この例外なら明示的にcatchしなくてよい
			throw new ArrayIndexOutOfBoundsException("サイト上の車番を持つ車が存在しません。");
		}
		return car.getDirection(cellI, cellJ, alpha);
	}

	// (α,β)に車番nをセットしようと試みる。(初期値設定用)
	// 指定された位置が空であればセットし、trueを返す(成功)。
	// すでに別の車がいればfalseを返す(失敗)
	public boolean initCar(int alpha, int beta, int num) {

		if (mu(alpha, beta)) {
			return false;
		}

		sites[alpha][beta] = num;
		return true;
	}


	/**
	 * 交差点サイトにいる車が動ける条件 M を返します。
	 *
	 * @param  alpha : 交差点番号
	 * @return 動ける条件の boolean 値
	 */
	private boolean toMove(int alpha) {

		int a   = alpha;			// α
		int ap  = (alpha + 1) % 4;	// α+
		int app = (alpha + 2) % 4;	// α++
		int am  = (alpha + 3) % 4;	// α-

		/*
		 * 注：演算子の優先順位
		 * 高
		 *   ! x    (論理否定)
		 *   x & y  (論理積)
		 *   x | y  (論理和)
		 * 低
		 */

		boolean b = mu(a,0) & !aa(a) & !mu(ap,0) | mu(a,0) & aa(a) & !mu(a,1)
				  | mu(a,0) & !aa(a) & mu(ap,0) & !aa(ap) & !mu(app,0)
				  | mu(a,0) & !aa(a) & mu(ap,0) & aa(ap) & !mu(ap,1)
				  | mu(a,0) & !aa(a) & mu(ap,0) & !aa(ap) & mu(app,0) & !aa(app) & !mu(am,0)
				  | mu(a,0) & !aa(a) & mu(ap,0) & !aa(ap) & mu(app,0) & aa(app) & !mu(app,1)
				  | mu(a,0) & !aa(a) & mu(ap,0) & !aa(ap) & mu(app,0) & !aa(app) & mu(am,0) & !aa(am)
				  | mu(a,0) & !aa(a) & mu(ap,0) & !aa(ap) & mu(app,0) & !aa(app) & mu(am,0) & aa(am) & !mu(am,1);

		return b;
	}

	/**
	 * 交差点サイトにいる車が動けない条件 S を返します。
	 *
	 * @param  alpha : 交差点番号
	 * @return 動けない条件の boolean 値
	 */
	private boolean toStop(int alpha) {

		int a   = alpha;			// α
		int ap  = (alpha + 1) % 4;	// α+
		int app = (alpha + 2) % 4;	// α++
		int am  = (alpha + 3) % 4;	// α-

		boolean b = mu(a,0) & aa(a) & mu(a,1)
				  | mu(a,0) & !aa(a) & mu(ap,0) & aa(ap) & mu(ap,1)
				  | mu(a,0) & !aa(a) & mu(ap,0) & !aa(ap) & mu(app,0) & aa(app) & mu(app,1)
				  | mu(a,0) & !aa(a) & mu(ap,0) & mu(app,0) & !aa(app) & mu(am,0) & aa(am) & mu(am,1);

		return b;
	}


	//交差点サイトの時間発展を行なう
	private void updateRoundabouts() {
		// このセルに交差点は4箇所ある。
		for (int a = 0; a < 4; a++) {
			// sites_newに直接更新
			sites_new[a][0] = ;

		}
	}


	// 時間発展
	// すべてupdateしてからすべてswapする必要がある
	public void updateCell() {

		//交差点サイトのアップデート


		// 道路サイトのアップデート


		swapBuffer();
	}


	//----- test -----
	public static void main(String[] args) {

	}

}
