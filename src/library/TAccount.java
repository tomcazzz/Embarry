package library;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class TAccount 
{
	// get array of google accounts
	public static Account[] getAccounts(Context context)
	{
		AccountManager am = AccountManager.get(context); // "this" references the current Context
		Account[] accounts = am.getAccountsByType("com.google");
		
		return accounts;
	}

	// get String array of google accounts
	public static String[] getAccountStrings(Context context)
	{		
		Account[] accounts = getAccounts(context);
		String[] sAccounts = new String[accounts.length];
		
		for(int i=0; i<accounts.length; i++) sAccounts[i] = accounts[i].name;
		
		return sAccounts;
	}
}
