package vcsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import base.Father;

import android.util.Log;

public class MisspellingTools
{
	protected double denominator;
	ArrayList<List<String>> sizes = new ArrayList<List<String>>();
	
	/**
	 * 
	 * @param word: reads in word as a string
	 * @return formattedString: converted string into List<String>
	 */
	
	private List<String> stringFormatter(String word)
	{
		word.toLowerCase();
		String[] splitWord = word.split("");
		List<String> listWord = new ArrayList<String>(Arrays.asList(splitWord));
		listWord.remove(0);
		sizes.add(listWord);
		denominator = listWord.size();
//	Father.print("There are " + denominator + " total letters");
		return listWord;
	}
	/**
	 * 
	 * @param rawWordArray: ArrayList<String> of letters from a word
	 * @return finalArray: ArrayList<String> of unique letters from a word
	 */

	private ArrayList<String> uniqueArrayCreator(List<String> rawWordArray)
	{
		ArrayList<String> finalArray = new ArrayList<String>(new HashSet<String>(rawWordArray));
		Collections.sort(finalArray);

		return finalArray;
	}
	
	/**
	 * 
	 * @param rawWordArray: ArrayList<String> of letters from a word
	 * @return frequencies: double[] of frequencies of unique letters within a word
	 */
	private double[] letterFrequencyCalculator(List<String>rawLetterArray, ArrayList<String> uniqueWordsArray)
	{
		//ArrayList<String> uniqueWordsArray = (ArrayList<String>) uniqueArrayCreator(rawLetterArray);
		double[] frequencies = new double[uniqueWordsArray.size()];
		int count = 0;
		int position = 0;
		while(position < uniqueWordsArray.size())
		{
			for(String uniqueWord:uniqueWordsArray)
			{
				for(String rawWord:rawLetterArray)
				{
					if(uniqueWord.equals(rawWord)&& position != uniqueWordsArray.size())
					{
						count++;
					}
				}
				frequencies[position] =  (count*1.0f);
				position++;
				count=0;
			}
		}
//		for(int j=0; j<frequencies.length; j++)
//		Log.d("debug", uniqueWordsArray.get(j)+ " has "+ frequencies[j]+" occurances");
		return frequencies;
	}
	/**
	 * 
	 * @param uniqueLetterFrequencies: double[] calculated from letterFrequencyCalculator(...)
	 * @param uniqueLettersList: ArrayList<String> calculated from uniqueArrayCreator(...) 
	 * @return proportions: double[] of the proportions of a unique letter within a word
	 */
	private double[] letterProportionCalculator(double[] uniqueLetterFrequencies, ArrayList<String> uniqueLettersList)
	{
		
		double[] proportions = new double[uniqueLettersList.size()];
//		Father.print("Denominator:"+denominator);
		for(int i=0; i<uniqueLetterFrequencies.length; i++)
		{
			proportions[i] = (double)(uniqueLetterFrequencies[i]/(double)denominator);
		}
		//prints results to log
//		for(int j=0; j<uniqueLetterFrequencies.length; j++)
//		{
//			Log.d("debug","Proportion for " +uniqueLettersList.get(j)+ ": " + proportions[j]);
//		}
		return proportions;
	}
	
	//used for entropy calculations
	private double log(double proportion)
	{
		return (Math.log(proportion)/Math.log(2.0));
	}
	
	public double oneWordEntropy(double[] proportions )
	{
		double entropy = 0.0;
		for(int i=0; i<proportions.length; i++)
		{
			entropy += proportions[i]*log(1/proportions[i]);
		}
//		Father.print("One word entropy is: "+entropy);
		return entropy;
	}
	
	private double sharedLettersCalculator(ArrayList<String> spoken, ArrayList<String> parameter)
	{
		int count = 0;
		for(String spokenLetter:spoken)
		{
			for(String paramLetter:parameter)
			{
				if(spokenLetter.equals(paramLetter))
				{
					count ++;
				}
			}
		}
//		Father.print("Shared Letters: " + count);
		return (double)count;
	}

	public double twoWordEntropy(ArrayList<String> spoken, ArrayList<String> parameter)
	{
		//check if shared is greater than or equal to 2, if not then either equal to 0 or 1
		double sharedLetters = sharedLettersCalculator(spoken, parameter);
		double spokenSize = (double)spoken.size();
		double paramSize = (double)parameter.size();
		double uniqueLetters = (spokenSize - sharedLetters) + (paramSize - sharedLetters);
		double doubleFreqCombos = (sharedLetters*.5)*(2.0*spokenSize + 2.0*paramSize - 3.0*sharedLetters - 2.0*uniqueLetters- 1.0);
		double singleFreqCombos = (spokenSize*paramSize)-(2.0*doubleFreqCombos);
		
		if((sharedLetters == 1 && (sizes.get(0).size()==1 && sizes.get(1).size()==1)))
		{
//			Father.print("Case1");
//			Father.print("The sizes are: " + sizes.get(0).size() + " and " + sizes.get(1).size());
//			Father.print("Two word entropy: " + 1.0);
			sizes.clear();
			return 1.0;
		}
		else if(sharedLetters == 0 && sizes.get(0).size() == 1 && sizes.get(1).size() == 1)
		{
//			Father.print("Case2");
//			Father.print("The sizes are: " + sizes.get(0).size() + " and " + sizes.get(1).size());
//			Father.print("Two Word Entropy: "+0.0);
			sizes.clear();
			return 0.0;
		}
		else
		{
//			Father.print("Case3");
//			Father.print("The sizes are: " + sizes.get(0).size() + " and " + sizes.get(1).size());
			double entropy = singleFreqCombos*(1.0/(spokenSize*paramSize))*log((1.0/(spokenSize*paramSize)))+ 
									doubleFreqCombos*(2.0/(spokenSize*paramSize))*log((2.0 /(spokenSize*paramSize)));
//			Father.print("Two Word Entropy is:" + entropy);
			sizes.clear();
			return entropy;
		}
	}

	public double mutualInformationCalculator(String spoken, String parameter)
	{
//		Father.print("The parameter word is: " +parameter);
		List<String> parameterList = stringFormatter(parameter);
		ArrayList<String> uniqueParameter = uniqueArrayCreator(parameterList);
		double[] parameterFreq = letterFrequencyCalculator(parameterList, uniqueParameter);
		double[] uniqueParameterProportions = letterProportionCalculator(parameterFreq, uniqueParameter);
		double parameterWordEntropy = oneWordEntropy(uniqueParameterProportions);
		
//		Father.print("The spoken word is: " +spoken);
		List<String> spokenList = stringFormatter(spoken);
		ArrayList<String> uniqueSpoken = uniqueArrayCreator(spokenList);
		double[] uniqueFreq = letterFrequencyCalculator(spokenList, uniqueSpoken);
		double[] uniqueSpokenProportions = letterProportionCalculator(uniqueFreq, uniqueSpoken);
		double uniqueWordEntropy = oneWordEntropy(uniqueSpokenProportions);
		
		double mutualEntropy = twoWordEntropy(uniqueSpoken, uniqueParameter);
		double finalResult = uniqueWordEntropy + parameterWordEntropy + mutualEntropy;
		return finalResult; 
	}
	
	public HashSet<String> bestMatches(String input, ArrayList<String> dictionary)
	{
		double theoreticalYield = mutualInformationCalculator(input, input);
		double allowableError = .60;
		double currentScore;
		double highestScore = 0;
		double tol = .000001;
		boolean firstTime = true;
		HashSet<String> matches = new HashSet<String>();
		for(String word:dictionary)
		{
			currentScore = mutualInformationCalculator(input, word); 
			if(firstTime||Math.abs(highestScore-currentScore) > tol && (currentScore>highestScore))
			{
//				Father.print("New highest found: "+ word+":"+ currentScore);
				firstTime = false;
				highestScore = currentScore;
				matches = new HashSet<String>();
				matches.add(word);
			}
			else
			{
				if(Math.abs(highestScore-currentScore) < tol )
				{
//					Father.print("Equivalent match found: "+word+":"+ currentScore);
					matches.add(word); 
				}
			}
		}
		Father.print("Ratio is: "+ ((Math.abs(theoreticalYield-highestScore))/theoreticalYield));
		
		if((Math.abs(theoreticalYield-highestScore))/theoreticalYield > allowableError || highestScore <=0)
		{
			HashSet<String> noMatches = new HashSet<String>();
			noMatches.add(input);
			return noMatches;
		}
		else
			return matches;
	}
	
	
	
}
