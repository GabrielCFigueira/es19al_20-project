package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import org.joda.time.DateTime;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;

public class BankOperationData {
    private String reference;
    private String type;
    private String sourceIban;
    private String targetIban;
    private Double value;
    private DateTime time;
    private String transactionSource;
    private String transactionReference;
    private String canRevert;

    public BankOperationData() {
    }

    public BankOperationData(Operation operation) {
        this.reference = operation.getReference();
        this.type = operation.getType().name();
        this.sourceIban = operation.getSourceIban();
        this.targetIban = operation.getTargetIban();
        this.value = new Double(operation.getValue()) / Bank.SCALE;
        this.time = operation.getTime();
        this.transactionSource = operation.getTransactionSource();
        this.transactionReference = operation.getTransactionReference();
        this.canRevert = operation.getCancellation();
    }

    public BankOperationData(String sourceIban, String targetIban, double value, String transactionSource, String transactionReference) {
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.value = value;
        this.transactionSource = transactionSource;
        this.transactionReference = transactionReference;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceIban() {
        return this.sourceIban;
    }

    public void setSourceIban(String sourceIban) {
        this.sourceIban = sourceIban;
    }

    public String getTargetIban() {
        return this.targetIban;
    }

    public void setTargetIban(String targetIban) {
        this.targetIban = targetIban;
    }

    public Double getValue() {
        return this.value;
    }

    public long getValueLong() {
        return Math.round(this.value);
    }

    public void setValue(long value) {
        this.value = Long.valueOf(value).doubleValue() * Bank.SCALE;
    }

    public DateTime getTime() {
        return this.time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getTransactionSource() {
        return this.transactionSource;
    }

    public void setTransactionSource(String transactionSource) {
        this.transactionSource = transactionSource;
    }

    public String getTransactionReference() {
        return this.transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getCanRevert() {
        return this.canRevert;
    }

    public void setCanRevert(String canRevert) {
        this.canRevert = canRevert;
    }

}
