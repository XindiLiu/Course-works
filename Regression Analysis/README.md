These are projects in Regression Analysis.
### Multiple Linear Regression
Prediction of BMF (body fat mass) on women.
The dataset was introduced in *Garcia, A. L., Wagner, K., Hothorn, T., Koebnick, C., Zunft, H.-J. F., and Trippo, U. (2005). Improved prediction of body fat by measuring skinfold thickness, circumferences, and bone breadths.* and can be found in the R-package `TH.data`.
This project includes:
>- Thorough residual analysis for model adequacy checking, including various types of residual scaling and plotting.
>- Diagnostics and handling of outliers, leverage and influential observations using e.g. Cook’s distance and CovRatio.
>- Possible transformations of the variables to correct model inadequacies.
>- Multicollinearity diagnostics and treatments.
>- Different types of variable selection using model evaluation criteria such as e.g. MSE, BIC, Mallows’ $C_p$ and adjusted $R^2$. Cross validation was used to evaluate MSE and adjusted $R^2$.
>- Bootstrap based confidence intervals for regression coefficients using the percentile method.

### General Linear Model
Price setting of travel insurance. 
The price model is 
\[price = \gamma_0 \prod_{k=1}^M \gamma_{k,i}\]
where $\gamma_0 $ is the base level and $\gamma_{k,i}, k = 1, ...,M $ are the risk factors corresponding to variable number $k$ and variable group number $i$.
The tasks were:
>- Grouping and risk differentiation
>- Leveling. Having found the risk factors $\gamma_{k,i}$, determine the base level $\gamma_0x$.