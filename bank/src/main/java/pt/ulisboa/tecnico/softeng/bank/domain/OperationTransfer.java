package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class OperationTransfer extends OperationTransfer_Base {
    
    public OperationTransfer(Account account, double value, String targetIBAN) {
        checkArguments(account, value, targetIBAN);

        setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
        setValue(value);
        setTime(DateTime.now());

        setAccount(account);

        setBank(account.getBank());
        setTargetIban(targetIBAN);
    }

    public String revert(){
        if(getCancellation().contains("_CANCEL"))
            throw new BankException();

        setCancellation(getReference() + "_CANCEL");
        for(Bank bank : FenixFramework.getDomainRoot().getBankSet()){
            Account account = bank.getAccount(this.getTargetIban());
            if (account != null) {
                return account.transfer(getValue(), this.getAccount()).getReference();
            }
        }
        throw new BankException();
    }

    private void checkArguments(Account account, double value, String targetIBAN){
        if (account == null || value <= 0 || targetIBAN == null || targetIBAN.isEmpty()) {
            throw new BankException();
        }
    }

    public String getType(){
        return "TRANSFER";
    }
}
