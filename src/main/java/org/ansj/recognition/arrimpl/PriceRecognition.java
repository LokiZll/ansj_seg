//package org.ansj.recognition.arrimpl;
//
//import org.ansj.domain.Term;
//import org.ansj.domain.TermNatures;
//import org.ansj.recognition.TermArrRecognition;
//import org.ansj.util.Graph;
//import org.ansj.util.TermUtil;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class PriceRecognition implements TermArrRecognition {
//
//	public static final Set<Character> PRICE_UNIT = new HashSet<>();
//
//	static {
//		PRICE_UNIT.add('元');
//		PRICE_UNIT.add('美金');
//		PRICE_UNIT.add('美元');
//		PRICE_UNIT.add('￥');
//		PRICE_UNIT.add('usd');
//		PRICE_UNIT.add('$');
//	};
//
//	public PriceRecognition() {
//	}
//
//	/**
//	 * 数字+数字合并,zheng
//	 *
//	 * @param graph
//	 */
//	@Override
//	public void recognition(Graph graph) {
//		Term[] terms = graph.terms ;
//		int length = terms.length - 1;
//		Term to;
//		Term temp;
//		for (int i = 0; i < length; i++) {
//			temp = terms[i];
//
//			if (temp == null) {
//				continue;
//			}
//
//			if (!temp.termNatures().numAttr.isNum() && !(temp.getNatureStr().endsWith("PlantNo") && "".equals(temp.getName().replaceAll("[0-9]", "")))) {
//				continue;
//			}
//
//			if (temp.termNatures() == TermNatures.M_ALB) { //阿拉伯数字
//				if(!temp.from().getName().equals(".") && temp.to().getName().equals(".")&&temp.to().to().termNatures()==TermNatures.M_ALB&&!temp.to().to().to().getName().equals(".")){
//					temp.setName(temp.getName()+temp.to().getName()+temp.to().to().getName());
//					terms[temp.to().getOffe()] = null ;
//					terms[temp.to().to().getOffe()] = null ;
//					TermUtil.termLink(temp, temp.to().to().to());
//					i = temp.getOffe() - 1;
//				}
//			}
//
//
//            if (quantifierRecognition) { //开启量词识别
//                to = temp.to();
//                if (to.termNatures().numAttr.isQua()) {
//                    linkTwoTerms(terms, temp, to);
//                    temp.setNature(to.termNatures().numAttr.nature);
//
//                    if ("m".equals(to.termNatures().numAttr.nature.natureStr)) {
//                        i = temp.getOffe() - 1;
//                    } else {
//                        i = to.getOffe();
//                    }
//                }
//            }
//
//
//        }
//
//    }
//
//	private void doLink(Term[] terms, Term temp, Set<Character> f_num) {
//		Term to;
//		if (f_num.contains(temp.getName().charAt(0))) {
//			to = temp.to();
//			while (to.getName().length() == 1 && f_num.contains(to.getName().charAt(0))) {
//				linkTwoTerms(terms, temp, to);
//				to = to.to();
//			}
//		}
//	}
//
//	private void linkTwoTerms(Term[] terms, Term temp, Term to) {
//        temp.setName(temp.getName() + to.getName());
//        Term origTo = terms[to.getOffe()];
//        if (origTo.getName().length() > to.getName().length()) {//to的位置被别的词占用了
//            //此修改基于TermUtil.insertTerm(Term[] terms, List tempList, TermNatures tns)的一个bug
//            //假设现在有4个term，ABCD，现在要把BC合成一个词，那么这个函数执行的结果是：
//            //terms[0]=A terms[1]=BC terms[3]=D
//            //A->B->C->D
//            //BC->D
//            int end = origTo.getName().length() + to.getOffe();
//            Term pre = to;
//            Term next = to.to();//从to.to开始找回原有的词
//            while (next != null && next.getOffe() < end) {
//                terms[next.getOffe()] = next;
//                pre = next;
//                next = next.to();
//            }
//            if (next != null)
//                TermUtil.termLink(pre, next);
//        }
//        terms[to.getOffe()] = null;
//        TermUtil.termLink(temp, to.to());
//    }
//
//
//}
