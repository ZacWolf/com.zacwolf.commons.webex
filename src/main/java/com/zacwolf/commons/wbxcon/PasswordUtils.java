/* @(#)PasswordUtils.java - zac@zacwolf.com
 *
 * Abstract class for specifying a specific type of Crypter
 * 
	Licensed under the MIT License (MIT)
	
	Copyright (c) 2014 Zac Morris
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
 */
package com.zacwolf.commons.wbxcon;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;

public class PasswordUtils {

final	public	static		String		LCaseChars		=	"abcdefgijkmnopqrstwxyz";
final	public	static		String		UCaseChars		=	"ABCDEFGHJKLMNPQRSTWXYZ";
final	public	static		String		NumericChars	=	"0123456789";
final	public	static		String		SpecialChars	=	"!\"#$%&'()*+,-./:;<=>?@[]^_`{|}~";
final	private	static		int			lcase			=	0;
final	private	static		int			ucase			=	1;
final	private	static		int			num				=	2;
final	private	static		int			special			=	3;

	public static String generateRandom(int minLength, int maxLength, int minLCaseCount, int minUCaseCount, int minNumCount, int minSpecialCount){
final	Map<Integer,Integer>	charGroupsUsed			=	new HashMap<Integer,Integer>();
								charGroupsUsed.put(lcase, minLCaseCount);
								charGroupsUsed.put(ucase, minUCaseCount);
								charGroupsUsed.put(num, minNumCount);
								charGroupsUsed.put(special, minSpecialCount);
final	char[] 					randomString;
		if (minLength < maxLength){
								randomString			=	new char[RandomUtils.nextInt(minLength, maxLength + 1)];
		} else {				randomString			=	new char[minLength];
		}
		int						requiredCharactersLeft	=	minLCaseCount + minUCaseCount + minNumCount + minSpecialCount;
		for (int i = 0; i < randomString.length; i++) {
final	StringBuilder			selectableChars			=	new StringBuilder();
			if (requiredCharactersLeft < randomString.length - i) {
								selectableChars.append(LCaseChars).append(UCaseChars).append(NumericChars).append(SpecialChars);
			} else {
				for (Integer k :charGroupsUsed.keySet()) {
					if (charGroupsUsed.get(k)>0)
						switch (k) {
							case lcase:
								selectableChars.append(LCaseChars);
								break;
							case ucase:
								selectableChars.append(UCaseChars);
								break;
							case num:
								selectableChars.append(NumericChars);
								break;
							case special:
								selectableChars.append(SpecialChars);
								break;
						}
				}
			}
final	char					nextChar				=	selectableChars.charAt(RandomUtils.nextInt(0, selectableChars.length()));
								randomString[i]			=	nextChar;
			if (LCaseChars.contains(String.valueOf(nextChar))){
								charGroupsUsed.put(lcase, charGroupsUsed.get(lcase)-1);
				if (charGroupsUsed.get(lcase) >= 0)
								requiredCharactersLeft--;
			} else if (UCaseChars.contains(String.valueOf(nextChar))){
								charGroupsUsed.put(ucase,charGroupsUsed.get(ucase)-1);
				if (charGroupsUsed.get(ucase) >= 0)
								requiredCharactersLeft--;
			} else if (NumericChars.contains(String.valueOf(nextChar))){
								charGroupsUsed.put(num,charGroupsUsed.get(num)-1);
				if (charGroupsUsed.get(num) >= 0)
								requiredCharactersLeft--;
			} else if (SpecialChars.contains(String.valueOf(nextChar))){
								charGroupsUsed.put(special,charGroupsUsed.get(special)-1);
				if (charGroupsUsed.get(special) >= 0)
								requiredCharactersLeft--;
			}
		}
		return new String(randomString);
	}

	public static void main(String[] args) {
		try{
			System.out.println(generateRandom(Integer.parseInt(args[0]),
											  Integer.parseInt(args[1]),
											  Integer.parseInt(args[2]),
											  Integer.parseInt(args[3]),
											  Integer.parseInt(args[4]),
											  Integer.parseInt(args[5])
											)
							  );
		} catch (Exception e){
			System.err.println("Required Arguments: int minLength, int maxLength, int minLCaseCount, int minUCaseCount, int minNumCount, int minSpecialCount");
			e.printStackTrace();
		}
	}
}
