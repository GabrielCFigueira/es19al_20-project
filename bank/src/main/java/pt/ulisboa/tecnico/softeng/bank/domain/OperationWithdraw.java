package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class OperationWithdraw extends OperationWithdraw_Base {
    
    public OperationWithdraw(Account account, long value) {
        checkArguments(account, value);

        setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
        setValue(value);
        setTime(DateTime.now());

        setAccount(account);

        setBank(account.getBank());
    }

    public String revert(){
        setCancellation(getReference() + "_CANCEL");
        return getAccount().deposit(getValue()).getReference();
    }

    private void checkArguments(Account account, long value){
        if (account == null || value <= 0) {
            throw new BankException();
        }
    }

    public String getType(){
        return "WITHDRAW";
    }
}
