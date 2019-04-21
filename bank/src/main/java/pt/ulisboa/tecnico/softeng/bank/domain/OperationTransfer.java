package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class OperationTransfer extends OperationTransfer_Base {
    
    public OperationTransfer(Account account, long value, String targetIBAN) {
        checkArguments(account, value, targetIBAN);

        setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
        setValue(value);
        setTime(DateTime.now());

        setAccount(account);

        setBank(account.getBank());
        setTargetIban(targetIBAN);
    }

    public String revert(){
        if(getTransactionSource() != null && getTransactionSource().equals("REVERT"))
            throw new BankException();

        for(Bank bank : FenixFramework.getDomainRoot().getBankSet()){
            Account account = bank.getAccount(this.getTargetIban());
            if (account != null) {
                Operation revertOperation = account.transfer(getValue(), this.getAccount());
                revertOperation.setTransactionSource("REVERT");
                revertOperation.setTransactionReference(getReference());
                return revertOperation.getReference();
            }
        }
        throw new BankException();
    }

    private void checkArguments(Account account, long value, String targetIBAN){
        if (account == null || value <= 0 || targetIBAN == null || targetIBAN.isEmpty()) {
            throw new BankException();
        }
    }

    public String getType(){
        return "TRANSFER";
    }
}
