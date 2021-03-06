package org.ripple.power.hft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum TransactionType {

	BUY("BUY"), SELL("SELL"), CREDIT("CREDIT"), DEBIT("DEBIT"), INTREST(
			"INTREST"), FEES("FEES"), REBALANCE("REBALANCE");

	private static Map<String, TransactionType> values = new LinkedHashMap<String, TransactionType>(
			9, 1);
	private static List<String> literals = new ArrayList<String>(9);
	private static List<String> names = new ArrayList<String>(9);
	private static List<TransactionType> valueList = new ArrayList<TransactionType>(
			9);

	static {
		synchronized (TransactionType.values) {
			TransactionType.values.put(BUY.enumValue, BUY);
			TransactionType.values.put(SELL.enumValue, SELL);
			TransactionType.values.put(CREDIT.enumValue, CREDIT);
			TransactionType.values.put(DEBIT.enumValue, DEBIT);
			TransactionType.values.put(INTREST.enumValue, INTREST);
			TransactionType.values.put(FEES.enumValue, FEES);
			TransactionType.values.put(REBALANCE.enumValue, REBALANCE);
		}
		synchronized (TransactionType.valueList) {
			TransactionType.valueList.add(BUY);
			TransactionType.valueList.add(SELL);
			TransactionType.valueList.add(CREDIT);
			TransactionType.valueList.add(DEBIT);
			TransactionType.valueList.add(INTREST);
			TransactionType.valueList.add(FEES);
			TransactionType.valueList.add(REBALANCE);
			TransactionType.valueList = Collections.unmodifiableList(valueList);
		}
		synchronized (TransactionType.literals) {
			TransactionType.literals.add(BUY.enumValue);
			TransactionType.literals.add(SELL.enumValue);
			TransactionType.literals.add(CREDIT.enumValue);
			TransactionType.literals.add(DEBIT.enumValue);
			TransactionType.literals.add(INTREST.enumValue);
			TransactionType.literals.add(FEES.enumValue);
			TransactionType.literals.add(REBALANCE.enumValue);
			TransactionType.literals = Collections.unmodifiableList(literals);
		}
		synchronized (TransactionType.names) {
			TransactionType.names.add("BUY");
			TransactionType.names.add("SELL");
			TransactionType.names.add("CREDIT");
			TransactionType.names.add("DEBIT");
			TransactionType.names.add("INTREST");
			TransactionType.names.add("FEES");
			TransactionType.names.add("REBALANCE");
			TransactionType.names = Collections.unmodifiableList(names);
		}
	}

	private final String enumValue;

	private TransactionType(String value) {
		this.enumValue = value;
	}

	public static TransactionType fromString(String name) {
		return TransactionType.valueOf(name);
	}

	public String value() {
		return this.enumValue;
	}

	public static TransactionType fromValue(String value) {
		for (TransactionType enumName : TransactionType.values()) {
			if (enumName.getValue().equals(value)) {
				return enumName;
			}
		}
		throw new IllegalArgumentException("TransactionType.fromValue(" + value
				+ ')');
	}

	public String getValue() {
		return this.enumValue;
	}

	public static List<String> literals() {
		return TransactionType.literals;
	}

	public static List<String> names() {
		return TransactionType.names;
	}

}
