package pt.ulisboa.tecnico.softeng.bank.services.remote.dataobjects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.domain.OperationTransfer;

public class RestBankOperationData {
	private String reference;
	private String type;
	private String sourceIban;
	private String targetIban;
	private Double value;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private DateTime time;
	private String transactionSource;
	private String transactionReference;

	public RestBankOperationData() {
	}

	public RestBankOperationData(Operation operation) {
		this.reference = operation.getReference();
		this.type = operation.getType();
		this.sourceIban = operation.getAccount().getIBAN();
		if(operation instanceof OperationTransfer)
			this.targetIban = ((OperationTransfer)operation).getTargetIban();
		this.value = operation.getValue();
		this.time = operation.getTime();
		this.transactionSource = operation.getTransactionSource();
		this.transactionReference = operation.getTransactionReference();
	}

	public RestBankOperationData(String iban, double value, String transactionSource, String transactionReference) {
		this.sourceIban = iban;
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

	/*--------- New functionality ---------*/
	public String getSourceIban() {
		return this.sourceIban;
	}

	public void setSourceIban(String iban) {
		this.sourceIban = iban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}

	public void setTargetIban(String iban) {
		this.targetIban = iban;
	}
	/*-------------------------------------*/

	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
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

}
