package com.alcatel.smartlinkv3.common.file;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("rawtypes")
public class FileSortUtils {

    public enum SortMethod {
        name, size, date, type
    }
    
	public static final String name_sort_string = "name";
	public static final String size_sort_string = "size";
	public static final String date_sort_string = "date";
	public static final String type_sort_string = "type";
	
	public static HashMap<String, SortMethod> sort_map = new HashMap<String, SortMethod>();	
	
	static
	{
		sort_map.put(name_sort_string, SortMethod.name);
		sort_map.put(size_sort_string, SortMethod.size);
		sort_map.put(date_sort_string, SortMethod.date);
		sort_map.put(type_sort_string, SortMethod.type);			
	}
	
	public static SortMethod getSortTypeByName(String sort) {
		return sort_map.get(sort);		
	}

    private SortMethod mSort;


	private HashMap<SortMethod, Comparator> mComparatorList = new HashMap<SortMethod, Comparator>();

    public FileSortUtils() {
        mSort = SortMethod.name;         
        mComparatorList.put(SortMethod.name, cmpName);
        mComparatorList.put(SortMethod.size, cmpSize);
        mComparatorList.put(SortMethod.date, cmpDate);
        mComparatorList.put(SortMethod.type, cmpType);
    }
    
    @SuppressWarnings("unchecked")
	public void sort(List<FileItem> items)
    {
    	Collections.sort(items, mComparatorList.get(mSort));    	
    }

    public void setSortMethod(SortMethod s) {
        mSort = s;
    }

    public SortMethod getSortMethod() {
        return mSort;
    }

	public Comparator getComparator() {
        return mComparatorList.get(mSort);
    }

  
	private abstract class FileComparator implements Comparator<FileItem> {

        @Override
        public int compare(FileItem file1, FileItem file2) {
			return doCompare(file1, file2);
        }

        protected abstract int doCompare(FileItem object1, FileItem object2);
    }
	
	
	private Comparator cmpName = new FileComparator() {		

		@Override
		protected int doCompare(FileItem object1, FileItem object2) {
			
			if(object1.isDir == true && object2.isDir == true) {
				return object1.name.compareToIgnoreCase(object2.name);
			}
			
			if(object1.isDir == true && object2.isDir == false) {
				return -1;
			}
			
			if(object1.isDir == false && object2.isDir == true) {
				return 1;
			}
			
			if(object1.isDir == false && object2.isDir== false) {
				return object1.name.compareToIgnoreCase(object2.name);
			}
			
			return -1;		
		}
	};
	
    private Comparator cmpSize = new FileComparator() {
  
    	@Override
		protected int doCompare(FileItem object1, FileItem object2) {
    		return 0;
    		//return longToCompareInt(object1.size - object2.size);			
		}
    };
    
    private Comparator cmpDate = new FileComparator() {      
		@Override
		protected int doCompare(FileItem object1, FileItem object2) {	
			return 0;
			//return longToCompareInt(object1.date - object2.date);			
		}
    };
    
    private Comparator cmpType = new FileComparator() {   
		
		  @Override
	        public int doCompare(FileItem object1, FileItem object2) {
	            int result =FileUtils.getExtFromFilename(object1.name).compareToIgnoreCase(
	            		FileUtils.getExtFromFilename(object2.name));
	            if (result != 0)
	                return result;

	            return object1.name.compareToIgnoreCase(object2.name);			
	        }
    };
	
/*    private int longToCompareInt(long result) {
        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }   
*/
}
 
