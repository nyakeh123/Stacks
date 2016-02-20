package uk.co.nyakeh.stacks.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import uk.co.nyakeh.stacks.Objects.StockPurchase;
import uk.co.nyakeh.stacks.database.StockDbSchema.StockPurchaseTable;

public class StockCursorWrapper extends CursorWrapper {

    public StockCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public StockPurchase getStockPurchases() {
        String id = getString(getColumnIndex(StockPurchaseTable.Cols.ID));
        String symbol = getString(getColumnIndex(StockPurchaseTable.Cols.SYMBOL));
        Long datePurchased = getLong(getColumnIndex(StockPurchaseTable.Cols.DATE_PURCHASED));
        int price = getInt(getColumnIndex(StockPurchaseTable.Cols.PRICE));
        int count = getInt(getColumnIndex(StockPurchaseTable.Cols.COUNT));
        double fee = getDouble(getColumnIndex(StockPurchaseTable.Cols.FEE));
        double total = getDouble(getColumnIndex(StockPurchaseTable.Cols.TOTAL));

        StockPurchase stockPurchase = new StockPurchase(UUID.fromString(id),symbol,new Date(datePurchased), price, count, fee, total);
        return stockPurchase;
    }
}
