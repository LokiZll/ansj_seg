package org.ansj.recognition.arrimpl;

import org.ansj.domain.NumNatureAttr;
import org.ansj.domain.Term;
import org.ansj.domain.TermNatures;
import org.ansj.recognition.TermArrRecognition;
import org.ansj.util.Graph;
import org.ansj.util.TermUtil;

import java.util.HashSet;
import java.util.Set;

public class NumRecognition implements TermArrRecognition {

	public static final Set<Character> j_NUM = new HashSet<>();
	public static final Set<Character> f_NUM = new HashSet<>();

	//价格单位
	public static final Set<String> PRICE_UNIT = new HashSet<>();
	//价格单位前缀
	public static final Set<String> PRICE_UNIT_PREFIX = new HashSet<>();

	//重量单位
	public static final Set<String> WEIGHT_UNIT = new HashSet<>();


	static {
		PRICE_UNIT.add("元");
		PRICE_UNIT.add("美金");
		PRICE_UNIT.add("美元");
		PRICE_UNIT.add("usd");
		PRICE_UNIT.add("USD");
		PRICE_UNIT_PREFIX.add("￥");
		PRICE_UNIT_PREFIX.add("$");

		WEIGHT_UNIT.add("柜");
		WEIGHT_UNIT.add("件");
		WEIGHT_UNIT.add("吨");
		WEIGHT_UNIT.add("千克");
		WEIGHT_UNIT.add("KG");
		WEIGHT_UNIT.add("kg");
		WEIGHT_UNIT.add("Kg");
	};

	static {
		j_NUM.add('零');
		j_NUM.add('一');
		j_NUM.add('两');
		j_NUM.add('二');
		j_NUM.add('三');
		j_NUM.add('四');
		j_NUM.add('五');
		j_NUM.add('六');
		j_NUM.add('七');
		j_NUM.add('八');
		j_NUM.add('九');
		j_NUM.add('十');
		j_NUM.add('百');
		j_NUM.add('千');
		j_NUM.add('万');
		j_NUM.add('亿');
		j_NUM.add('○');

		f_NUM.add('零');
		f_NUM.add('壹');
		f_NUM.add('贰');
		f_NUM.add('叁');
		f_NUM.add('肆');
		f_NUM.add('伍');
		f_NUM.add('陆');
		f_NUM.add('柒');
		f_NUM.add('捌');
		f_NUM.add('玖');
		f_NUM.add('拾');
		f_NUM.add('佰');
		f_NUM.add('仟');
		f_NUM.add('万');
		f_NUM.add('亿');

	}

	;

	private boolean quantifierRecognition;

	public NumRecognition(boolean quantifierRecognition) {
		this.quantifierRecognition = quantifierRecognition;
	}

	/**
	 * 数字+数字合并,zheng
	 *
	 * @param graph
	 */
	@Override
	public void recognition(Graph graph) {
		Term[] terms = graph.terms ;
		int length = terms.length - 1;
		Term to;
		Term temp;
		for (int i = 0; i < length; i++) {
			temp = terms[i];

			if (temp == null) {
				continue;
			}

			if (!temp.termNatures().numAttr.isNum() && !(temp.getNatureStr().endsWith("PlantNo") && "".equals(temp.getName().replaceAll("[0-9]", "")))) {
				continue;
			}

            if (temp.getName().length() == 1) {
				doLink(terms, temp, j_NUM);

				if(temp.getName().length()>1){
					i-- ;
					continue;
				}

				doLink(terms, temp, f_NUM);
				if (temp.getName().length() > 1) {
                    i--;
                    continue;
                }
            }


			if (temp.termNatures() == TermNatures.M_ALB) { //阿拉伯数字
				if(!temp.from().getName().equals(".") && temp.to().getName().equals(".")&&temp.to().to().termNatures()==TermNatures.M_ALB&&!temp.to().to().to().getName().equals(".")){
					temp.setName(temp.getName()+temp.to().getName()+temp.to().to().getName());
					terms[temp.to().getOffe()] = null ;
					terms[temp.to().to().getOffe()] = null ;
					TermUtil.termLink(temp, temp.to().to().to());
					i = temp.getOffe() - 1;
				}
			}

			if(temp.getNatureStr().endsWith("PlantNo")){
				if(!temp.from().getName().equals(".") && temp.to().getName().equals(".")&&temp.to().to().termNatures()==TermNatures.M_ALB&&!temp.to().to().to().getName().equals(".")){
					temp.setName(temp.getName()+temp.to().getName()+temp.to().to().getName());
					terms[temp.to().getOffe()] = null ;
					terms[temp.to().to().getOffe()] = null ;
					TermUtil.termLink(temp, temp.to().to().to());
					temp.setNature(TermNatures.M.nature);
					temp.termNatures().numAttr=NumNatureAttr.NUM;
					i = temp.getOffe() - 1;
				}
			}


            if (quantifierRecognition) { //开启量词识别
                to = temp.to();
				//自定义识别 start
				if(PRICE_UNIT.contains(temp.to().getName())){
					terms[temp.getOffe()] = TermUtil.makeNewTermNum(temp,temp.to(),TermNatures.PRICE);
					i = to.getOffe();
					continue;
				}

				if(PRICE_UNIT_PREFIX.contains(temp.from().getName())){
					terms[temp.from().getOffe()] = TermUtil.makeNewTermNum(temp.from(),temp,TermNatures.PRICE);
					i = temp.getOffe();
					continue;
				}

				if(WEIGHT_UNIT.contains(temp.to().getName())){
					terms[temp.getOffe()] = TermUtil.makeNewTermNum(temp,temp.to(),TermNatures.WEIGHT);
					i = to.getOffe();
					continue;
				}
				//自定义识别 end


                if (to.termNatures().numAttr.isQua()) {
                    linkTwoTerms(terms, temp, to);
                    temp.setNature(to.termNatures().numAttr.nature);

                    if ("m".equals(to.termNatures().numAttr.nature.natureStr)) {
                        i = temp.getOffe() - 1;
                    } else {
                        i = to.getOffe();
                    }
                }
            }


        }

    }

	private void doLink(Term[] terms, Term temp, Set<Character> f_num) {
		Term to;
		if (f_num.contains(temp.getName().charAt(0))) {
			to = temp.to();
			while (to.getName().length() == 1 && f_num.contains(to.getName().charAt(0))) {
				linkTwoTerms(terms, temp, to);
				to = to.to();
			}
		}
	}

	private void linkTwoTerms(Term[] terms, Term temp, Term to) {
        temp.setName(temp.getName() + to.getName());
        Term origTo = terms[to.getOffe()];
        if (origTo.getName().length() > to.getName().length()) {//to的位置被别的词占用了
            //此修改基于TermUtil.insertTerm(Term[] terms, List tempList, TermNatures tns)的一个bug
            //假设现在有4个term，ABCD，现在要把BC合成一个词，那么这个函数执行的结果是：
            //terms[0]=A terms[1]=BC terms[3]=D
            //A->B->C->D
            //BC->D
            int end = origTo.getName().length() + to.getOffe();
            Term pre = to;
            Term next = to.to();//从to.to开始找回原有的词
            while (next != null && next.getOffe() < end) {
                terms[next.getOffe()] = next;
                pre = next;
                next = next.to();
            }
            if (next != null)
                TermUtil.termLink(pre, next);
        }
        terms[to.getOffe()] = null;
        TermUtil.termLink(temp, to.to());
    }


}
