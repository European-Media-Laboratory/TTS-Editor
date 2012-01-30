package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;

import ttsEngines.TTSEngine;
import auxillary.Services;


public class TestServices {

	@Test
	public void test() {
		assertNotNull(Services.getG2PService());
		assertNotNull(Services.getRecognizer());
		Collection<TTSEngine> engines = Services.getAvailableTTSEngines();
		assertNotNull(engines);
		assertEquals(2, engines.size());
	}

}
