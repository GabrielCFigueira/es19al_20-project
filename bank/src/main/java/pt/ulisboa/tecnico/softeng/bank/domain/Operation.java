package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public abstract class Operation extends Operation_Base {
	protected Operation(){ }

	public Operation(Account account, double value) {
		checkArguments(account, value);

		setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
		setValue(value);
		setTime(DateTime.now());

		setAccount(account);

		setBank(account.getBank());
	}

	public void delete() {
		setBank(null);
		setAccount(null);

		deleteDomainObject();
	}

	private void checkArguments(Account account, double value){ }

	public abstract String revert();

}
