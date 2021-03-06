package pub.sanalar.wms.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import pub.sanalar.wms.models.WmsCategory;

@Transactional
public class CategoryQueryDao extends HibernateDaoSupport {

	public Map<Integer, String> getTopCategories(){
		String hql = "select cat.categoryId, cat.categoryName from WmsCategory cat where cat.wmsCategory=null";
		List<?> categories = getHibernateTemplate().find(hql);
		
		Map<Integer, String> results = new HashMap<Integer, String>();
		for(int i=0; i < categories.size(); ++i){
			Object[] category = (Object[])categories.get(i);
			results.put((Integer)category[0], (String)category[1]);
		}
		
		return results;
	}
	
	public Map<Integer, String> getTopCategoryIcons(){
		Map<Integer, String> results = new HashMap<Integer, String>();
		results.put(1, "fa fa-flask");
		results.put(2, "fa fa-cutlery");
		results.put(3, "fa fa-futbol-o");
		results.put(4, "fa fa-tv");
		results.put(5, "fa fa-medkit");
		return results;
	}
	
	public Map<Integer, String> getSubCategories(Integer parentId){
		String hql = "select cat.categoryId, cat.categoryName from WmsCategory cat where cat.wmsCategory.categoryId=?";
		List<?> categories = getHibernateTemplate().find(hql, parentId);
		
		Map<Integer, String> results = new HashMap<Integer, String>();
		for(int i=0; i < categories.size(); ++i){
			Object[] category = (Object[])categories.get(i);
			results.put((Integer)category[0], (String)category[1]);
		}
		
		return results;
	}
	
	public String getTopCategoryListString(Map<Integer, String> categoryList){
		StringBuilder sb = new StringBuilder("[");
		
		for(String name : categoryList.values()){
			sb.append("\"" + name + "\",");
		}
		
		sb.setCharAt(sb.length() - 1, ']');
		return sb.toString();
	}
	
	private StringBuilder getSubCategoryListStringOfOneKind(Integer parentId){
		Map<Integer, String> categoryList = getSubCategories(parentId);
		StringBuilder sb = new StringBuilder("[");
		
		for(String name : categoryList.values()){
			sb.append("\"" + name + "\",");
		}
		
		sb.setCharAt(sb.length() - 1, ']');
		return sb;
	}
	
	public String getAllSubCategoryListString(Map<Integer, String> categoryList){
		StringBuilder sb = new StringBuilder("[");
		
		for(Integer id : categoryList.keySet()){
			sb.append(getSubCategoryListStringOfOneKind(id));
			sb.append(',');
		}
		
		sb.setCharAt(sb.length() - 1, ']');
		return sb.toString();
	}
	
	public WmsCategory getCategoryByName(String categoryName){
		String hql = "from WmsCategory cat where cat.categoryName=?";
		@SuppressWarnings("unchecked")
		List<WmsCategory> cat = (List<WmsCategory>) getHibernateTemplate().find(hql, categoryName);
		
		if(cat.size() == 0){
			return null;
		}
		
		return cat.get(0);
	}
	
	public WmsCategory getCategoryByCategoryString(String category){
		if(category == null){
			return null;
		}
		
		if(!category.contains(" > ")){
			return null;
		}
		
		String categoryName = category.substring(category.indexOf('>') + 2).trim();
		return getCategoryByName(categoryName);
	}
}
