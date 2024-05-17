package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EconomyManager_H2 extends AbstractEconomyManager_SQL {

    public EconomyManager_H2(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat,
                             String separator, String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
    }

    @Override
    public void Open() {
        if (!this.Initialize())
            return;

        try {
            this._connection = DriverManager.getConnection("jdbc:h2:file:" + new File("plugins//ServerSystem", "economy.h2").getAbsolutePath());
        } catch (SQLException var2) {
            this._plugin.Error("Could not establish an H2 connection, SQLException: " + var2.getMessage());
        }
    }

    @Override
    protected boolean Initialize() {
        try {
            Class.forName("serversystem.libs.org.h2.Driver");
            return true;
        } catch (ClassNotFoundException var2) {
            this._plugin.Error("H2 driver class missing: " + var2.getMessage() + ".");
            return false;
        }
    }

    @Override
    public void FetchTopTen() {

    }
}
