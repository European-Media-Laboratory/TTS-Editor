package test;

import java.util.List;

import auxillary.Services.Parameter;

import ttsEngines.Converter;
import ttsEngines.Language;
import ttsEngines.TTSEngine;

public class DummyTTSWithParams implements TTSEngine {
	
	public DummyTTSWithParams(@Parameter("foo") String foo) { 
		System.out.println("Called ctor with " + foo);
	}

	public List<Language> getLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

	public Converter getConverter() {
		// TODO Auto-generated method stub
		return null;
	}

}
