package roundabout;

/**
 * 印南論文 第4章 ラウンドアバウト交差点ルールの実装
 * @author T. Miyazaki
 *
 */
public class Cell {

	// 車情報のリストへのアクセス
	public static Car[] carList;

	private int m;

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
	private void swapBuffer() {
		if (swapFlag) {
			sites = sites1;
			sites_new = sites2;
		} else {
			sites = sites2;
			sites_new = sites1;
		}
		swapFlag = !swapFlag;
	}


	// コンストラクタ
	public Cell(int m) {
		this.m = m;

		sites1 = new int[4][m + 1];
		sites2 = new int[4][m + 1];
		swapBuffer();

		// 接続先のセル
	}


	// μ_(α,β)^t を返します。
	public boolean mu(int alpha, int beta) {
		return (sites[alpha][beta] != 0);
	}

	// n_(α,β)^t を返します。
	public int num(int alpha, int beta) {
		return sites[alpha][beta];
	}


	// 交差点にいる車が動ける条件 M
	private boolean canMove(int alpha) {

		int a   = alpha;			// α
		int ap  = (alpha + 1) % 4;	// α+
		int app = (alpha + 2) % 4;	// α++
		int am  = (alpha + 3) % 4;	// α-

		boolean b = mu(a,0) & ...

		return b;
	}

	// 交差点にいる車が動けない条件 S
	//private boolean cannotMove()


	// 時間発展
	public void update() {

		//交差点サイトのアップデート


		// 道路サイトのアップデート


		swapBuffer();
	}


	//----- test -----
	public static void main(String[] args) {

	}

}
