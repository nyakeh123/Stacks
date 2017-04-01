package uk.co.nyakeh.stacks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.nyakeh.stacks.database.StockLab;
import uk.co.nyakeh.stacks.objects.Dividend;
import uk.co.nyakeh.stacks.objects.Metadata;
import uk.co.nyakeh.stacks.objects.StockPurchase;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IAsyncTask {
    private TextView mDiff;
    private TextView mPercentageChange;
    private TextView mDividendYield;
    private TextView mDividendPercentageYield;
    private TextView mPortfolio;
    private TextView mPercentageFI;
    private TextView mDaysSinceInvestment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(toolbar);
        SetupNavigation(toolbar);

        mDiff = (TextView) findViewById(R.id.dashboard_diff);
        mPercentageChange = (TextView) findViewById(R.id.dashboard_percentageChange);
        mDividendYield = (TextView) findViewById(R.id.dashboard_dividendYield);
        mDividendPercentageYield = (TextView) findViewById(R.id.dashboard_dividendPercentageYield);
        mPortfolio = (TextView) findViewById(R.id.dashboard_portfolio);
        mPercentageFI = (TextView) findViewById(R.id.dashboard_percentageFI);
        mDaysSinceInvestment = (TextView) findViewById(R.id.dashboard_daysSinceInvestment);
        new GoogleFinanceClient(this, this).execute("LON:VMID");
    }

    public void PostExecute(String response) {
        double purchaseSum = 0;
        int purchasedStockQuantity = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(1900, 01, 01);
        Date latestInvestment = cal.getTime();
        List<StockPurchase> stockPurchaseHistory = StockLab.get(this).getStockPurchaseHistory();
        for (StockPurchase stockPurchase : stockPurchaseHistory) {
            purchaseSum += stockPurchase.Total;
            purchasedStockQuantity += stockPurchase.Quantity;
            if (stockPurchase.DatePurchased.after(latestInvestment)) {
                latestInvestment = stockPurchase.DatePurchased;
            }
        }

        Metadata metadata = StockLab.get(this).getMetadata();

        double dividendSum = 0;
        List<Dividend> dividendHistory = StockLab.get(this).getDividendHistory();
        for (Dividend dividend : dividendHistory) {
            dividendSum += dividend.Amount;
        }

        try {
            JSONObject share = new JSONObject(response);
            double sharePrice = Double.parseDouble(share.get("l").toString());
            double portfolioSum = sharePrice * purchasedStockQuantity;
            double changeInValue = portfolioSum - purchaseSum;
            double percentageChange = (changeInValue / purchaseSum) * 100;
            double percentageFI = (portfolioSum / metadata.FinancialIndependenceNumber) * 100;
            double dividendPercentageYield = (dividendSum / portfolioSum) * 100;
            mDiff.setText(getString(R.string.money_format, changeInValue));
            mPercentageChange.setText(getString(R.string.percentage_format, percentageChange));
            mDividendYield.setText(getString(R.string.money_format, dividendSum));
            mDividendPercentageYield.setText(getString(R.string.percentage_format, dividendPercentageYield));
            mPortfolio.setText(getString(R.string.money_format, portfolioSum));
            mPercentageFI.setText(getString(R.string.percentage_fi, percentageFI));

            long diff = new Date().getTime() - latestInvestment.getTime();
            int daysSinceInvestment = (int) (diff / (24 * 60 * 60 * 1000));
            mDaysSinceInvestment.setText(getString(R.string.days_format, daysSinceInvestment));
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private void SetupNavigation(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dashboard_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.dashboard_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_fund) {
            Intent intent = new Intent(this, FundActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_purchaseHistory) {
            Intent intent = new Intent(this, StockPurchaseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_dividend) {
            Intent intent = new Intent(this, DividendActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_controlPanel) {
            Intent intent = new Intent(this, ControlPanelActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dashboard_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}