package org.ansj.library;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.util.MyStaticValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nlpcn.commons.lang.tire.domain.Forest;

import java.util.List;

public class DicLibraryTest {

	@Before
	public void init(){
		MyStaticValue.ENV.put(DicLibrary.DEFAULT,"library/beefPlantNoLibrary.dic");
		Forest forest = DicLibrary.get();
		if(forest==null){
			DicLibrary.put(DicLibrary.DEFAULT,DicLibrary.DEFAULT,new Forest());
		}
	}

	/**
	 * 关键词增加
	 *
	 * @param keyword 所要增加的关键词
	 * @param nature 关键词的词性
	 * @param freq 关键词的词频
	 */
	@Test
	public void insertTest() {
		DicLibrary.insert(DicLibrary.DEFAULT, "增加新词", "我是词性", 1000);
		Result parse = DicAnalysis.parse("这是用户自定义词典增加新词的例子");
		System.out.println(parse);
		boolean flag = false;
		for (Term term : parse) {
			flag = flag || "增加新词".equals(term.getName());
		}
		Assert.assertTrue(flag);

	}

	/**
	 * 增加关键词
	 *
	 * @param keyword
	 */
	@Test
	public void insertTest2() {
		DicLibrary.insert(DicLibrary.DEFAULT, "增加新词");
		Result parse = DicAnalysis.parse("这是用户自定义词典增加新词的例子");
		System.out.println(parse);
		boolean flag = false;
		for (Term term : parse) {
			flag = flag || "增加新词".equals(term.getName());
		}
		Assert.assertTrue(flag);
	}

	/**
	 * 删除关键词
	 */
	@Test
	public void delete() {
		insertTest();
		DicLibrary.delete(DicLibrary.DEFAULT, "增加新词");
		Result parse = DicAnalysis.parse("这是用户自定义词典增加新词的例子");
		System.out.println(parse);
		boolean flag = false;
		for (Term term : parse) {
			flag = flag || "增加新词".equals(term.getName());
		}
		Assert.assertFalse(flag);
	}

	/**
	 * 自定义key
	 */
	@Test
	public void keyTest(){
		String key = "dic_mykey" ;
		DicLibrary.put(key, key, new Forest());
		DicLibrary.insert(key, "增加新词", "我是词性", 1000);
		Result parse = DicAnalysis.parse("这是用户自定义词典增加新词的例子",DicLibrary.gets(key));
		System.out.println(parse);
		boolean flag = false;
		for (Term term : parse) {
			flag = flag || "增加新词".equals(term.getName());
		}
		Assert.assertTrue(flag);
	}

	@Test
	public void libraryTest(){
		Result parse = DicAnalysis.parse("肩胛229件\n");
//		Result parse = DicAnalysis.parse("1014 1.1 八件套天津一柜");
//		List<Term> termList = DicAnalysis.parse("阿根廷3676上脑盖").getTerms();
		System.out.println(parse);
		boolean flag = false;
		for (Term term : parse) {
			flag = flag || "增加新词".equals(term.getName());
		}
		Assert.assertTrue(flag);
	}
}
