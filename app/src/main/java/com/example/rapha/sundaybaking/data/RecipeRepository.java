package com.example.rapha.sundaybaking.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.rapha.sundaybaking.AppExecutors;
import com.example.rapha.sundaybaking.data.local.RecipeDatabase;
import com.example.rapha.sundaybaking.data.models.Ingredient;
import com.example.rapha.sundaybaking.data.models.InstructionStep;
import com.example.rapha.sundaybaking.data.models.Recipe;
import com.example.rapha.sundaybaking.data.remote.RecipesRemoteAPI;
import com.example.rapha.sundaybaking.util.EspressoIdlingResource;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import timber.log.Timber;

public class RecipeRepository implements RecipeDataSource {

    private final RecipesRemoteAPI recipesRemoteAPI;
    private final AppExecutors appExecutors;
    private final RecipeDatabase recipeDatabase;

    private final MediatorLiveData<List<Recipe>> observedRecipes = new MediatorLiveData<>();
    private final MutableLiveData<DataState> dataState = new MutableLiveData<>();

    private static RecipeRepository INSTANCE;

    private RecipeRepository(RecipesRemoteAPI recipesRemoteAPI, RecipeDatabase recipeDatabase, AppExecutors appExecutors) {
        this.recipesRemoteAPI = recipesRemoteAPI;
        this.recipeDatabase = recipeDatabase;
        this.appExecutors = appExecutors;
    }

    public static RecipeRepository getInstance(final AppExecutors appExecutors, final RecipesRemoteAPI recipesRemoteAPI, final RecipeDatabase recipeDatabase) {
        if (INSTANCE == null) {
            synchronized (RecipeRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecipeRepository(recipesRemoteAPI, recipeDatabase, appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<Recipe>> getRecipes() {
        LiveData<List<Recipe>> recipesInDatabase = getDatabaseSource();
        observedRecipes.addSource(recipesInDatabase, recipes -> {
            if (recipes == null || recipes.size() == 0) {
                fetchRecipes();
                observedRecipes.removeSource(recipesInDatabase);
                observedRecipes.addSource(getDatabaseSource(), observedRecipes::setValue);
            } else observedRecipes.setValue(recipes);
        });
        return observedRecipes;
    }

    private LiveData<List<Recipe>> getDatabaseSource() {
        return recipeDatabase.recipesDao().getRecipes();
    }

    public LiveData<List<Ingredient>> getIngredients(String recipeName) {
        return recipeDatabase.ingredientsDao().getIngredients(recipeName);
    }

    public LiveData<List<InstructionStep>> getInstructionSteps(String recipeName) {
        return recipeDatabase.instructionStepsDao().getInstructionSteps(recipeName);
    }

    public LiveData<InstructionStep> getInstructionStep(String recipeName, int stepNo) {
        return recipeDatabase.instructionStepsDao().getInstructionStep(recipeName, stepNo);
    }

    public List<Ingredient> getIngredientsSync(String recipeName) {
        return recipeDatabase.ingredientsDao().getIngredientList(recipeName);
    }

    private void fetchRecipes() {
        Timber.d("Fetching recipes");
        dataState.setValue(DataState.FETCHING);
        appExecutors.diskIO().execute(() -> {
            EspressoIdlingResource.increment();
            Response<List<Recipe>> response;
            try {
                response = recipesRemoteAPI.getRecipes().execute();
                List<Recipe> recipes = response.body();
                if (recipes != null) {
                    recipeDatabase.recipesDao().insertRecipes(recipes);
                    for (Recipe recipe : recipes) {
                        List<Ingredient> ingredients = recipe.getIngredients();
                        for (Ingredient ingredient : ingredients) {
                            ingredient.setRecipeName(recipe.getName());
                        }
                        recipeDatabase.ingredientsDao().insertIngredients(ingredients);
                        List<InstructionStep> instructionSteps = recipe.getSteps();
                        for (InstructionStep step : instructionSteps) {
                            step.setRecipeName(recipe.getName());
                        }
                        recipeDatabase.instructionStepsDao().insertInstructionSteps(instructionSteps);
                    }
                    dataState.postValue(DataState.SUCCESS);
                }
            } catch (IOException e) {
                Timber.e(e, "error fetching recipe data from remote source");
                dataState.postValue(DataState.ERROR);
            }
            EspressoIdlingResource.decrement();
        });
    }

    public void triggerFetch() {
        fetchRecipes();
    }

    public LiveData<DataState> getDataState() {
        return dataState;
    }
}
