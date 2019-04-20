package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import java.util.Map;
import java.util.TreeMap;


import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer;

public class TaxPayerData {


	private String nif;
	private String name;
	private String address;
	private Map<Integer, Double> taxesAsSeller = new TreeMap<Integer, Double>();
  private Map<Integer, Double> taxesAsBuyer = new TreeMap<Integer, Double>();
	public TaxPayerData() {
	}

	public TaxPayerData(TaxPayer taxPayer) {
		this.nif = taxPayer.getNif();
		this.name = taxPayer.getName();
		this.address = taxPayer.getAddress();
		this.taxesAsSeller = convertToDouble(taxPayer.getToPayPerYear());
		this.taxesAsBuyer = convertToDouble(taxPayer.getTaxReturnPerYear());
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

	public Map<Integer, Double> getTaxesAsBuyer() {
		return this.taxesAsBuyer;
	}

	public void setTaxesAsBuyer(Map<Integer, Double> taxes) {
		this.taxesAsBuyer = taxes;
	}

	public Map<Integer, Double> getTaxesAsSeller() {
		return this.taxesAsSeller;
	}

	public void setTaxesAsSeller(Map<Integer, Double> taxes) {
		this.taxesAsSeller = taxes;
	}

	private Map<Integer, Double> convertToDouble(Map<Integer, Long> longMap) {
		Map<Integer, Double> res = new TreeMap<Integer, Double>(); //FIXME treemap?
		for (int i : longMap.keySet())
			res.put(i, (double) longMap.get(i) / 1000);
		return res;
	}

}
