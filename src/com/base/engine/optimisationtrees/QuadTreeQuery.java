package com.base.engine.optimisationtrees;


public interface QuadTreeQuery{


	public Object check(QuadNode node, Object o1);
	public Object returnData(QuadNode node);
}
