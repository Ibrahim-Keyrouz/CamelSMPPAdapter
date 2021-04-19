package com.byblosbank.camelsmppadapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

import com.byblosbank.camelsmppadapter.utilities.StringUtils;

@SpringBootApplication
public class CamelSmppAdapterApplication {

	public static void main(String[] args) {
		System.out.println("bob "+1);
		SpringApplication.run(CamelSmppAdapterApplication.class, args);
	}

	/**
	 * Creates a UUID from raw string without dashes
	 * 
	 * @param raw UUID without dashes
	 * @return
	 */
	public static java.util.UUID toUUID(String raw) {
		StringUtils.notBlank(raw, "'raw' cannot be empty");
		StringUtils.hasRequiredLength(raw, 32, "'raw' must be of length 32");
		return java.util.UUID.fromString( //
				raw.replaceFirst( //
						"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", //
						"$1-$2-$3-$4-$5"));
	}

	@Nullable
	public static ZonedDateTime toZonedDateTime(LocalDateTime dateTime) {
		if (dateTime == null)
			return null;
		TimeZone timeZone = LocaleContextHolder.getTimeZone();
		return dateTime.atZone(timeZone.toZoneId());
	}

	@Nullable
	public static LocalDateTime toLocalDateTime(Instant instant) {
		if (instant == null)
			return null;
		TimeZone timeZone = LocaleContextHolder.getTimeZone();
		return LocalDateTime.ofInstant(instant, timeZone.toZoneId());
	}

}
