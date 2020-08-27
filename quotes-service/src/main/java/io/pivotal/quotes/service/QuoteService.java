package io.pivotal.quotes.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.pivotal.quotes.domain.*;
import io.pivotal.quotes.exception.SymbolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * A service to retrieve Company and Quote information.
 * 
 * @author David Ferreira Pinto
 *
 */
@Service
@RefreshScope
@Slf4j
public class QuoteService {

	@Value("${pivotal.quotes.quote_url}")
	protected String quote_url;

	@Value("${pivotal.quotes.quotes_url}")
	protected String quotes_url;

	@Value("${pivotal.quotes.companies_url}")
	protected String company_url;

	public static final String FMT = "json";
	private Random random = new Random();

	/*
         * cannot autowire as don't want ribbon here.
	 */
	private RestTemplate restTemplate = new RestTemplate();

	/**
	 * Retrieves an up to date quote for the given symbol.
	 * 
	 * @param symbol
	 *            The symbol to retrieve the quote for.
	 * @return The quote object or null if not found.
	 * @throws SymbolNotFoundException
	 */
	@HystrixCommand(fallbackMethod = "getQuoteFallback")
	public Quote getQuote(String symbol) throws SymbolNotFoundException {

		log.debug("QuoteService.getQuote: retrieving quote for: " + symbol);

		Map<String, String> params = new HashMap<String, String>();
		params.put("symbol", symbol);

//		IexQuote quote = restTemplate.getForObject(quote_url, IexQuote.class, params);
//
//		if (quote.getSymbol() == null) {
//			throw new SymbolNotFoundException("Symbol not found: " + symbol);
//		}
//
//		log.debug("QuoteService.getQuote: retrieved quote: " + quote);


//		return QuoteMapper.INSTANCE.mapFromIexQuote(quote);

		return quoteGenerator(symbol);
	}

	@SuppressWarnings("unused")
	private Quote getQuoteFallback(String symbol)
			throws SymbolNotFoundException {
		log.debug("QuoteService.getQuoteFallback: circuit opened for symbol: "
				+ symbol);
		Quote quote = new Quote();
		quote.setSymbol(symbol);
		quote.setStatus("FAILED");
		return quote;
	}

	/**
	 * Retrieves a list of CompanyInfor objects. Given the name parameters, the
	 * return list will contain objects that match the search both on company
	 * name as well as symbol.
	 * 
	 * @param name
	 *            The search parameter for company name or symbol.
	 * @return The list of company information.
	 */
	@HystrixCommand(fallbackMethod = "getCompanyInfoFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.timeout.enabled", value="false")
		    })
	public List<CompanyInfo> getCompanyInfo(String name) {
		log.debug("QuoteService.getCompanyInfo: retrieving info for: "
				+ name);
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("name", name);
//		CompanyInfo[] companies = restTemplate.getForObject(company_url,
//				CompanyInfo[].class, params);
		List<CompanyInfo> companies = new ArrayList<>();
		int noOfCompanies = random.nextInt(10);
		for (int count=0; count<noOfCompanies; count++) {
			companies.add(companyInfoGenerator(name));
		}
		log.debug("QuoteService.getCompanyInfo: retrieved info: "
				+ companies);
		return companies;
	}

	/**
	 * Retrieve multiple quotes at once.
	 * 
	 * @param symbols
	 *            comma delimeted list of symbols.
	 * @return a list of quotes.
	 */
	public List<Quote> getQuotes(String symbols) {
		log.debug("retrieving multiple quotes for: " + symbols);

//		IexBatchQuote batchQuotes = restTemplate.getForObject(quotes_url, IexBatchQuote.class, symbols);

//		log.debug("Got response: " + batchQuotes);
		final List<Quote> quotes = new ArrayList<>();

		Arrays.asList(symbols.split(",")).forEach(symbol -> {
//			if(batchQuotes.containsKey(symbol)) {
//				quotes.add(QuoteMapper.INSTANCE.mapFromIexQuote(batchQuotes.get(symbol).get("quote")));
				quotes.add(quoteGenerator(symbol));
//			} else {
//				log.warn("Quote could not be found for the following symbol: " + symbol);
//				Quote quote = new Quote();
//				quote.setSymbol(symbol);
//				quote.setStatus("FAILED");
//				quotes.add(quote);
//			}
		});

		return quotes;
	}


	@SuppressWarnings("unused")
	private List<CompanyInfo> getCompanyInfoFallback(String symbol)
			throws SymbolNotFoundException {
		log.debug("QuoteService.getCompanyInfoFallback: circuit opened for symbol: "
				+ symbol);
		List<CompanyInfo> companies = new ArrayList<>();
		return companies;
	}

	private Quote quoteGenerator(String symbol) {
		Quote quote = new Quote();
		quote.setSymbol(symbol);
		quote.setHigh(new BigDecimal(Math.random()));
		quote.setName(symbol);
		quote.setOpen(new BigDecimal(Math.random()));
		quote.setHigh(new BigDecimal(Math.random()));
		quote.setLow(new BigDecimal(Math.random()));
		quote.setChange(new BigDecimal(Math.random()));
		quote.setChangePercent(new BigDecimal(Math.random()).floatValue());
		quote.setMarketCap(new BigDecimal(Math.random()).floatValue());

			quote.setLastPrice(new BigDecimal(Math.random()));
			quote.setTimestamp(Date.from(Instant.now()));

		quote.setStatus("SUCCESS");
		quote.setVolume((int) new BigDecimal(Math.random()).doubleValue());

		return quote;
	}

	private CompanyInfo companyInfoGenerator(String symbol) {
		CompanyInfo company = new CompanyInfo();
		company.setSymbol(symbol);
		company.setName("RandomName"+Math.random());
		company.setExchange("No Exchange set");

		return company;
	}
}
