package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer;
import pt.ulisboa.tecnico.softeng.tax.domain.Seller;
import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer;

public class TaxPayerData {
	public enum Type {
		BUYER, SELLER
	}

	private String nif;
	private String name;
	private String address;
	private Type type;
	private Map<Integer, Double> taxes = new TreeMap<Integer, Double>();

	public TaxPayerData() {
	}

	public TaxPayerData(TaxPayer taxPayer) {
		Seller seller;
		Buyer buyer;
		this.nif = taxPayer.getNif();
		this.name = taxPayer.getName();
		this.address = taxPayer.getAddress();
		this.type = taxPayer instanceof Buyer ? Type.BUYER : Type.SELLER;
		if (taxPayer instanceof Seller) {
			seller = (Seller) taxPayer;
			this.taxes = convertToDouble(seller.getToPayPerYear());
		} else {
			buyer = (Buyer) taxPayer;
			this.taxes = convertToDouble(buyer.getTaxReturnPerYear());
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Map<Integer, Double> getTaxes() {
		return this.taxes;
	}

	public void setTaxes(Map<Integer, Double> taxes) {
		this.taxes = taxes;
	}

	private Map<Integer, Double> convertToDouble(Map<Integer, Long> longMap) {
		Map<Integer, Double> res = new TreeMap<Integer, Double>(); //FIXME treemap?
		for (int i : longMap.keySet())
			res.put(i, (double) longMap.get(i) / 1000);
		return res;
	}

}
