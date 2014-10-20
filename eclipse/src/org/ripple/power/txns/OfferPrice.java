package org.ripple.power.txns;

import com.ripple.client.Client;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.Issue;
import com.ripple.core.coretypes.STArray;
import com.ripple.core.coretypes.STObject;
import com.ripple.core.types.known.sle.entries.Offer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.ui.RPClient;

import java.math.BigDecimal;
import java.util.ArrayList;

public abstract class OfferPrice {

	public boolean subscribe = false;
	
	public String highBuy;

	public String hightSell;

	public String spread;

	public abstract void buy(Offer offer);

	public abstract void sell(Offer offer);

	public abstract void empty();

	public abstract void error(JSONObject obj);

	public abstract void complete(ArrayList<String> buys,
			ArrayList<String> sells);

	public static void load(String address, String buyCurName,
			String sellCurName, OfferPrice price) {
		AccountID account = AccountID.fromAddress(address);
		RPClient ripple = RPClient.ripple();
		if (ripple != null) {
			Issue buy = account.issue(buyCurName);
			Issue sell = account.issue(sellCurName);
			load(ripple.getClinet(), buy, sell, price);
		}
	}

	public static class OrderBooks {
		public static interface BookEvents {
			public void onUpdate(OrderBooks book);
		}

		private Client client;
		private final BookEvents callback;
		public Issue first, second;
		public STArray asks, bids;
		public Amount ask, bid, spread;

		public OrderBooks(Client client, Issue first, Issue second,
				BookEvents callback) {
			this.client = client;
			this.first = first;
			this.second = second;
			this.callback = callback;
		}

		private void calculateStats() {
			Offer firstAsk = (Offer) asks.get(0);
			Offer firstBid = (Offer) bids.get(0);
			BigDecimal askQuality = firstAsk.askQuality();
			BigDecimal bidQuality = firstBid.bidQuality();
			Amount secondOne = firstAsk.paysOne();
			ask = secondOne.multiply(askQuality);
			bid = secondOne.multiply(bidQuality);
			spread = ask.subtract(bid).abs();
		}

		private void requestUpdate(final OfferPrice price) {
			for (int i = 0; i < 2; i++) {
				final boolean getAsks = i == 0, getBids = !getAsks;
				Issue getIssue = getAsks ? first : second, payIssue = getAsks ? second
						: first;
				Request request = null;
				if (price.subscribe) {
					request = client.subscribeBookOffers(getIssue, payIssue);
				} else {
					request = client.requestBookOffers(getIssue, payIssue);
				}
				request.once(Request.OnResponse.class,
						new Request.OnResponse() {
							@Override
							public void called(Response response) {
								if (response.succeeded) {

									System.out.println("update");
									JSONArray offersJSON = response.result
											.optJSONArray("offers");
									STArray offers = STArray.translate
											.fromJSONArray(offersJSON);

									if (getBids) {
										bids = offers;
									} else {
										asks = offers;
									}
									if (retrievedBothBooks()) {
										if (!isEmpty()) {
											calculateStats();
										}
										callback.onUpdate(OrderBooks.this);
									}
								} else {
									price.error(response.message);
								}
							}
						});
				request.request();
			}
		}

		public boolean retrievedBothBooks() {
			return asks != null && bids != null;
		}

		public boolean isEmpty() {
			return !retrievedBothBooks() || asks.isEmpty() || bids.isEmpty();
		}
	}

	private static void load(Client client, final Issue first,
			final Issue second, final OfferPrice price) {
		if (price == null) {
			return;
		}
		new OrderBooks(client, first, second, new OrderBooks.BookEvents() {
			@Override
			public void onUpdate(OrderBooks book) {
				ArrayList<String> buys = new ArrayList<String>(100);
				ArrayList<String> sells = new ArrayList<String>(100);
				if (!book.isEmpty()) {
					price.highBuy = book.ask.toText();
					price.hightSell = book.bid.toText();
					price.spread = book.spread.toText();
					// buy
					for (STObject offer : book.asks) {
						Offer o = (Offer) offer;
						price.sell(o);
						BigDecimal payForOne = o.askQuality();
						Amount paysOne = o.paysOne();
						Amount getsOne = o.getsOne();
						sells.add(o.takerGets().toText() + " Sell "
								+ (o.takerPays().toText()) + " ("
								+ getsOne.toText() + "=="
								+ paysOne.multiply(payForOne).toText() + ")");
					}
					// sell
					for (STObject offer : book.bids) {
						Offer o = (Offer) offer;
						price.buy(o);
						BigDecimal payForOne = o.askQuality();
						Amount paysOne = o.paysOne();
						Amount getsOne = o.getsOne();
						buys.add(o.takerGets().toText() + " Buy "
								+ (o.takerPays().toText()) + " ("
								+ getsOne.toText() + "=="
								+ paysOne.multiply(payForOne).toText() + ")");
					}
				} else {
					// empty
					price.empty();
				}
				price.complete(buys, sells);
			}
		}).requestUpdate(price);
	}

}