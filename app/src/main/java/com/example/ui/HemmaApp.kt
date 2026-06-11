package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ChatMessage
import com.example.MainViewModel
import com.example.Screen
import com.example.data.*
import com.example.ui.theme.*
import androidx.compose.ui.platform.LocalDensity
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HemmaApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedProductDetail by viewModel.selectedProductDetail.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            HemmaNavigationBar(
                currentScreen = currentScreen,
                cartCount = cartItems.sumOf { it.quantity },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    is Screen.Home -> HomeScreen(viewModel)
                    is Screen.Catalogue -> CatalogueScreen(viewModel)
                    is Screen.Planner -> PlannerScreen(viewModel)
                    is Screen.AIAssistant -> AIAssistantScreen(viewModel)
                    is Screen.Cart -> ShoppingCartScreen(viewModel)
                    is Screen.Profile -> ProfileScreen(viewModel)
                }
            }

            // Product Detail Sheet / Overlay
            selectedProductDetail?.let { product ->
                ProductDetailOverlay(
                    product = product,
                    viewModel = viewModel,
                    onDismiss = { viewModel.showProductDetails(null) }
                )
            }
        }
    }
}

// Custom Scandia themed Bottom Navigation
@Composable
fun HemmaNavigationBar(
    currentScreen: Screen,
    cartCount: Int,
    onNavigate: (Screen) -> Unit
) {
    Column(modifier = Modifier.background(Color.White)) {
        HorizontalDivider(thickness = 1.dp, color = SoftGray)
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.navigationBarsPadding().height(72.dp)
        ) {
            NavigationBarItem(
                selected = currentScreen is Screen.Home,
                onClick = { onNavigate(Screen.Home) },
                label = { Text("Showroom", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Explore, contentDescription = "Showroom") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IkeaBlue,
                    selectedTextColor = IkeaBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = LightCream
                ),
                modifier = Modifier.testTag("nav_showroom")
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Catalogue,
                onClick = { onNavigate(Screen.Catalogue) },
                label = { Text("Shop", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shop") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IkeaBlue,
                    selectedTextColor = IkeaBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = LightCream
                ),
                modifier = Modifier.testTag("nav_shop")
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Planner,
                onClick = { onNavigate(Screen.Planner) },
                label = { Text("Plan", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Layers, contentDescription = "Planner") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IkeaBlue,
                    selectedTextColor = IkeaBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = LightCream
                ),
                modifier = Modifier.testTag("nav_planner")
            )
            NavigationBarItem(
                selected = currentScreen is Screen.AIAssistant,
                onClick = { onNavigate(Screen.AIAssistant) },
                label = { Text("Designer", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Face, contentDescription = "Designer") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IkeaBlue,
                    selectedTextColor = IkeaBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = LightCream
                ),
                modifier = Modifier.testTag("nav_designer")
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Cart,
                onClick = { onNavigate(Screen.Cart) },
                label = { Text("Cart", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                icon = {
                    BadgedBox(badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = IkeaYellow, contentColor = Color.Black) {
                                Text(cartCount.toString(), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IkeaBlue,
                    selectedTextColor = IkeaBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = LightCream
                ),
                modifier = Modifier.testTag("nav_cart")
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Profile,
                onClick = { onNavigate(Screen.Profile) },
                label = { Text("Profile", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IkeaBlue,
                    selectedTextColor = IkeaBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = LightCream
                ),
                modifier = Modifier.testTag("nav_profile")
            )
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Brand Title Welcome as seen in Editorial Aesthetic HTML
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            Text(
                text = "Hemma",
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                color = IkeaBlue,
                letterSpacing = (-1.5).sp // tracking-tighter
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search circular button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, SoftGray, CircleShape)
                        .clickable {
                            viewModel.navigateTo(Screen.Catalogue)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ScandiCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Person / Profile circular button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, SoftGray, CircleShape)
                        .clickable {
                            viewModel.navigateTo(Screen.Profile)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = ScandiCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Editorial Splash banner: "Nordic Slow Living" (replaces older winter cozy)
        EditorialCampaignCard(
            title = "Nordic Slow Living",
            subtitle = "The Malmö Collection starting at £49.",
            tag = "New Season",
            accentColor = IkeaBlue,
            onClick = {
                viewModel.selectedStyleFilter.value = "Scandi"
                viewModel.selectedCategory.value = "Living Room"
                viewModel.navigateTo(Screen.Catalogue)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Editorial Dual Shortcut Grid: Planner & AI Designer
        EditorialShortcutGrid(
            onNavigatePlanner = { viewModel.navigateTo(Screen.Planner) },
            onNavigateAI = { viewModel.navigateTo(Screen.AIAssistant) }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Multi Room inspiration selector row
        Text(
            "Explore by Room Space",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ScandiCharcoal,
            modifier = Modifier.testTag("home_section_category")
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val rooms = listOf(
                "Living Room" to Icons.Default.Chair,
                "Bedroom" to Icons.Default.Bed,
                "Storage" to Icons.Default.AllInbox,
                "Lighting" to Icons.Default.Lightbulb,
                "Outdoor" to Icons.Default.Yard
            )
            rooms.forEach { (name, icon) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, SoftGray, RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.selectedCategory.value = name
                            viewModel.navigateTo(Screen.Catalogue)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = name, tint = IkeaBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ScandiCharcoal)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Trending Decor horizontal scroll
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending flat-pack favourites", 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                color = ScandiCharcoal
            )
            Text(
                "See all",
                color = IkeaBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    viewModel.selectedCategory.value = null
                    viewModel.navigateTo(Screen.Catalogue)
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductCatalog.items.take(5).forEach { product ->
                TrendingProductCard(product = product, onClick = {
                    viewModel.showProductDetails(product)
                })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Small space hacks article card
        CompactEditorialCard(
            title = "Small Space Hacks with KALLAX",
            desc = "Learn how to use shelving units as smart room-dividers for studio flats.",
            onClick = {
                viewModel.showProductDetails(ProductCatalog.items.find { it.id == "kallax_shelving" })
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun EditorialShortcutGrid(
    onNavigatePlanner: () -> Unit,
    onNavigateAI: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card 1: Room Planner in Editorial Yellow
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(IkeaYellow)
                .clickable { onNavigatePlanner() }
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.Black.copy(alpha = 0.05f),
                    radius = 64.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(size.width + 8.dp.toPx(), size.height + 8.dp.toPx())
                )
            }
            
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Architecture",
                        tint = IkeaBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Room\nPlanner",
                        color = IkeaBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }
                Text(
                    text = "3D Visualizer",
                    color = IkeaBlue.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        // Card 2: AI Interior Designer in White
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, SoftGray),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onNavigateAI() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "AI Assistant",
                        tint = ScandiCharcoal,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    // Live green indicator
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(100.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color(0xFF2E7D32),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column {
                    Text(
                        text = "AI Interior\nDesigner",
                        color = ScandiCharcoal,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "Ask for suggestions",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Dot options
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFF5F5F2)))
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFE8E6E1)))
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFFD180)))
                }
            }
        }
    }
}

@Composable
fun EditorialCampaignCard(
    title: String,
    subtitle: String,
    tag: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp), // rounded-3xl equivalent
        border = BorderStroke(1.dp, SoftGray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Grid styling reproducing the html nested elements exactly
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightCream)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(120f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(100f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }
            }
            
            // Content layer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(IkeaBlue, shape = RoundedCornerShape(100.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "NEW SEASON",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
                
                Column {
                    Text(
                        text = "Nordic\nSlow Living",
                        color = ScandiCharcoal,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 35.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The Malmö Collection starting at £49.",
                        color = ScandiCharcoal.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier
                            .background(ScandiCharcoal, shape = RoundedCornerShape(100.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Shop the look",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingProductCard(product: Product, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ScandiOak),
        border = BorderStroke(1.dp, SoftGray),
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            // Elegant furniture vector placeholder mockup box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(LightCream)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(product.category) {
                        "Bedroom" -> Icons.Default.Bed
                        "Storage" -> Icons.Default.AllInbox
                        "Lighting" -> Icons.Default.Lightbulb
                        "Outdoor" -> Icons.Default.Yard
                        else -> Icons.Default.Chair
                    },
                    contentDescription = null,
                    tint = IkeaBlue,
                    modifier = Modifier.size(42.dp)
                )
            }
            // Product info
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = ScandiCharcoal
                )
                Text(
                    product.category,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "£${DecimalFormat("#.##").format(product.price)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = IkeaBlue,
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(IkeaBlue)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CompactEditorialCard(title: String, desc: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SoftGray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IkeaYellow.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = IkeaBlue, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ScandiCharcoal)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, fontSize = 12.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// --- PRODUCT CATALOGUE SCREEN ---
@Composable
fun CatalogueScreen(viewModel: MainViewModel) {
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val maxPrice by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
    val maxDiff by viewModel.maxDifficultyFilter.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.selectedStyleFilter.collectAsStateWithLifecycle()
    var filterExpanded by remember { mutableStateOf(false) }

    val filteredProducts = ProductCatalog.filterProducts(
        category = selectedCat,
        maxPrice = maxPrice,
        difficulty = maxDiff,
        styleFilter = selectedStyle
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Appbar filter hub
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = selectedCat ?: "All Furniture",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = IkeaBlue
                )
                Text(
                    "Discover ${filteredProducts.size} custom flat-packs",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            
            Button(
                onClick = { filterExpanded = !filterExpanded },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (filterExpanded) IkeaBlue else ScandiOak,
                    contentColor = if (filterExpanded) Color.White else ScandiCharcoal
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Filter", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Filters")
            }
        }

        // Expanded filter options
        AnimatedVisibility(visible = filterExpanded) {
            Surface(
                color = ScandiOak,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Category buttons
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IkeaBlue)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val categories = listOf("All", "Living Room", "Bedroom", "Storage", "Lighting", "Outdoor")
                        categories.forEach { cat ->
                            val isSel = (cat == "All" && selectedCat == null) || (cat == selectedCat)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) IkeaBlue else Color.White)
                                    .clickable {
                                        viewModel.selectedCategory.value = if (cat == "All") null else cat
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(cat, color = if (isSel) Color.White else ScandiCharcoal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Style filter
                    Text("Design Style", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IkeaBlue)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val styles = listOf("All", "Scandi", "Minimalist", "Industrial")
                        styles.forEach { style ->
                            val isSel = (style == "All" && selectedStyle == null) || (style == selectedStyle)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) IkeaBlue else Color.White)
                                    .clickable {
                                        viewModel.selectedStyleFilter.value = if (style == "All") null else style
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(style, color = if (isSel) Color.White else ScandiCharcoal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Assembly difficulty slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Max Assembly Difficulty", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IkeaBlue)
                            Text("1 = Easy, 5 = Sophisticated swedish setup", fontSize = 10.sp, color = Color.Gray)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                                .clickable {
                                    // Cycles difficulty (null, 2, 3, 4, 5)
                                    val current = maxDiff
                                    viewModel.maxDifficultyFilter.value = when (current) {
                                        null -> 2
                                        2 -> 3
                                        3 -> 4
                                        4 -> 5
                                        else -> null
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (maxDiff == null) "Show All" else "≤ $maxDiff Stars",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Reset Filters
                    Text(
                        "Reset all filters",
                        fontSize = 11.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectedCategory.value = null
                                viewModel.maxPriceFilter.value = null
                                viewModel.maxDifficultyFilter.value = null
                                viewModel.selectedStyleFilter.value = null
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Product Grid
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LayersClear, contentDescription = null, sizeFilter = 64, tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No products match. Try resetting filters!", color = Color.Gray)
                }
            }
        } else {
            LazyHorizontalGridOrVerticalGrid(products = filteredProducts, viewModel = viewModel)
        }
    }
}

@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, sizeFilter: Int, tint: Color) {
    Icon(imageVector, contentDescription, tint = tint, modifier = Modifier.size(sizeFilter.dp))
}

@Composable
fun LazyHorizontalGridOrVerticalGrid(products: List<Product>, viewModel: MainViewModel) {
    // Elegant list layout scroll
    Box(modifier = Modifier.fillMaxSize()) {
        val state = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state)
        ) {
            // Group lists in pairs for vertical catalog grid aesthetic
            val chunks = products.chunked(2)
            chunks.forEach { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column Card
                    Box(modifier = Modifier.weight(1f)) {
                        ProductGridItem(product = pair[0], onClick = { viewModel.showProductDetails(pair[0]) })
                    }
                    // Right Column Card
                    Box(modifier = Modifier.weight(1f)) {
                        if (pair.size > 1) {
                            ProductGridItem(product = pair[1], onClick = { viewModel.showProductDetails(pair[1]) })
                        } else {
                            Spacer(Modifier.fillMaxWidth())
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProductGridItem(product: Product, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ScandiOak),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            // Header Indicators Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(IkeaBlue.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                // Flat pack indicator badge
                if (product.isFlatPack) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Archive, contentDescription = "Flat-pack", tint = AccentOrange, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Flat-pack", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                        }
                    }
                }
                
                // Difficulty Icon
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(4.dp))
                        .background(IkeaBlue)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Diff: ${product.assemblyDifficulty}/5", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                // Central furniture Icon
                Icon(
                    imageVector = when(product.category) {
                        "Bedroom" -> Icons.Default.Bed
                        "Storage" -> Icons.Default.AllInbox
                        "Lighting" -> Icons.Default.Lightbulb
                        "Outdoor" -> Icons.Default.Yard
                        else -> Icons.Default.Chair
                    },
                    contentDescription = null,
                    tint = IkeaBlue,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }

            // Desc Body details
            Column(modifier = Modifier.padding(10.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.dimensionsDisplay, fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                
                // Stars rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = IkeaYellow, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${product.rating} (${product.reviewsCount})", fontSize = 10.sp, color = Color.DarkGray)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "£${DecimalFormat("#.##").format(product.price)}",
                        fontWeight = FontWeight.Black,
                        color = IkeaBlue,
                        fontSize = 15.sp
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(IkeaBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "See details", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

// --- ROOM PLANNER SCREEN (CORE FEATURE) ---
@Composable
fun PlannerScreen(viewModel: MainViewModel) {
    val activeRoom by viewModel.activeRoom.collectAsStateWithLifecycle()
    val savedRooms by viewModel.savedRooms.collectAsStateWithLifecycle()
    val placements by viewModel.plcItemsState.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedPlacementIndex.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("My Scandinavian Space") }
    var newRoomWidth by remember { mutableStateOf("400") }
    var newRoomLength by remember { mutableStateOf("400") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // App header containing saved plans switcher
        Card(
            shape = RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp),
            colors = CardDefaults.cardColors(containerColor = ScandiOak),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("HEMMA Room Planner", fontSize = 22.sp, fontWeight = FontWeight.Black, color = IkeaBlue)
                        Text("Design, scale and shop in complete 2D layout", fontSize = 12.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Plan", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Setup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Saved rooms horizontal scroll choices
                Text("Select active canvas:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IkeaBlue)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    savedRooms.forEach { room ->
                        val isOp = activeRoom?.id == room.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, if (isOp) IkeaBlue else Color.Transparent)
                                .background(if (isOp) IkeaBlue.copy(alpha = 0.15f) else Color.White)
                                .clickable { viewModel.loadRoomToPlanner(room) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = IkeaBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    room.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOp) IkeaBlue else ScandiCharcoal
                                )
                                if (!room.name.contains("Demo")) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Plan",
                                        tint = Color.Red.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.deleteRoom(room) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        activeRoom?.let { room ->
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scaled canvas draw
            Text(
                "Active Canvas: ${room.name} (${room.widthCm}×${room.lengthCm} cm)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = ScandiCharcoal
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Live Grid Canvas
            IndoorGridCanvas(
                room = room,
                placements = placements,
                selectedIndex = selectedIndex,
                onSelectPlacement = { viewModel.selectPlacement(it) },
                onUpdatePlacement = { index, x, y -> viewModel.updateActivePlacementPosition(x, y) }
            )

            // Canvas Tools Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = ScandiOak)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Selected Item Panel
                    if (selectedIndex != null && selectedIndex!! in placements.indices) {
                        val activeItem = placements[selectedIndex!!]
                        val itemProduct = ProductCatalog.items.find { it.id == activeItem.productId }
                        if (itemProduct != null) {
                            Text(
                                "Editing Item: ${itemProduct.name}",
                                fontWeight = FontWeight.Bold,
                                color = IkeaBlue,
                                fontSize = 14.sp
                            )
                            Text(
                                "Position: ${activeItem.x}x, ${activeItem.y}y cm | Rotation: ${activeItem.rotationDeg}°",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Rotate button
                                Button(
                                    onClick = { viewModel.rotateActivePlacement() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue)
                                ) {
                                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rotate", fontSize = 11.sp)
                                }
                                
                                // Delete button
                                Button(
                                    onClick = { viewModel.removeActivePlacement() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", fontSize = 11.sp)
                                }

                                // Info button
                                Button(
                                    onClick = { viewModel.showProductDetails(itemProduct) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = ScandiCharcoal)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "Specs")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Specs", fontSize = 11.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))

                            // Smart micro-move arrow keys
                            Text("Fine-move layout alignment:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IkeaBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(onClick = { viewModel.updateActivePlacementPosition(activeItem.x, activeItem.y - 10) }) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = "Up")
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(onClick = { viewModel.updateActivePlacementPosition(activeItem.x - 10, activeItem.y) }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Left")
                                }
                                Spacer(modifier = Modifier.width(40.dp))
                                IconButton(onClick = { viewModel.updateActivePlacementPosition(activeItem.x + 10, activeItem.y) }) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Right")
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(onClick = { viewModel.updateActivePlacementPosition(activeItem.x, activeItem.y + 10) }) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = "Down")
                                }
                            }
                        }
                    } else {
                        // Empty choice options
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = IkeaBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap any furniture piece on wood floor canvas to adjust, rotate, or delete", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // Quick shelf selection to place instantly in the Room!
            Text(
                "IKEA Flat-packs Picker toolbar:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = IkeaBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProductCatalog.items.forEach { product ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(IkeaBlue.copy(alpha = 0.08f))
                            .clickable { viewModel.addFurnitureToRoom(product.id) }
                            .padding(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when(product.category) {
                                    "Bedroom" -> Icons.Default.Bed
                                    "Storage" -> Icons.Default.AllInbox
                                    "Lighting" -> Icons.Default.Lightbulb
                                    "Outdoor" -> Icons.Default.Yard
                                    else -> Icons.Default.Chair
                                },
                                contentDescription = null,
                                tint = IkeaBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product.name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("£${product.price}", fontSize = 10.sp, color = Color.DarkGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Optimise or Add Layout to bag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        // Ask Gemini about active layout
                        val namesList = placements.map { 
                            ProductCatalog.getProductById(it.productId)?.name ?: "Furniture"
                        }.joinToString(", ")
                        viewModel.sendChatMessage("Analyze my active room setup called '${room.name}' measuring ${room.widthCm}x${room.lengthCm} cm: Currently placed items: $namesList. Suggest visual improvements or optimizations!")
                        viewModel.navigateTo(Screen.AIAssistant)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ScandiCharcoal),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Optimize", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Optimize Space")
                }

                Button(
                    onClick = {
                        viewModel.addEntireRoomToCart()
                        viewModel.navigateTo(Screen.Cart)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IkeaYellow, contentColor = Color.Black),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = "Buy Room", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buy Entire Set", fontWeight = FontWeight.Bold)
                }
            }
        } ?: run {
            // No Room selected (fallback setup list)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.LayersClear, contentDescription = null, sizeFilter = 64, tint = Color.LightGray)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No active rooms. Click 'New Setup' above!", color = Color.Gray)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }

    // Modal dialog to name and select rooms dims
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Plan Your Nordic Space", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("Name", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = newRoomName,
                        onValueChange = { newRoomName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_room_title"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Width (cm)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = newRoomWidth,
                                onValueChange = { newRoomWidth = it },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("new_room_w")
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Length (cm)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = newRoomLength,
                                onValueChange = { newRoomLength = it },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("new_room_l")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = newRoomWidth.toIntOrNull() ?: 400
                        val l = newRoomLength.toIntOrNull() ?: 400
                        viewModel.createAndOpenNewRoom(newRoomName, w, l)
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue)
                ) {
                    Text("Launch Canvas")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

// Visual 2D Floor builder of wood grain texture
@Composable
fun IndoorGridCanvas(
    room: SavedRoom,
    placements: List<PlacedFurniture>,
    selectedIndex: Int?,
    onSelectPlacement: (Int) -> Unit,
    onUpdatePlacement: (Int, Int, Int) -> Unit
) {
    val density = LocalDensity.current.density
    // Determine canvas height scaling dynamically based on room aspect ratio
    val aspectRatio = room.widthCm.toFloat() / room.lengthCm.toFloat()
    
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .aspectRatio(aspectRatio.coerceIn(0.6f, 1.6f)),
        border = BorderStroke(2.dp, IkeaBlue.copy(alpha = 0.4f))
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Draw nice wood planks background texture using Canvas lines
                    drawRect(color = Color(0xFFFBF8F4)) // light birch backing color
                    val plankSize = 25f
                    var offset = 0f
                    while (offset < size.width) {
                        drawLine(
                            color = Color(0xFFECE5D9),
                            start = Offset(offset, 0f),
                            end = Offset(offset, size.height),
                            strokeWidth = 1f
                        )
                        offset += plankSize
                    }
                    // Draw structural grid dots
                    val gridWidth = size.width / 10f
                    val gridHeight = size.height / 10f
                    for (i in 1..9) {
                        for (j in 1..9) {
                            drawCircle(
                                color = Color(0xFFD6C8B5),
                                radius = 2f,
                                center = Offset(i * gridWidth, j * gridHeight)
                            )
                        }
                    }
                }
        ) {
            val widthPx = maxWidth
            val heightPx = maxHeight

            // Scaling factors from real CM to layout pixels
            val scaleX = widthPx.value / room.widthCm.toFloat()
            val scaleY = heightPx.value / room.lengthCm.toFloat()

            // Draw each placed furniture as its scaled rectangular representation with text label and color
            placements.forEachIndexed { index, item ->
                val product = ProductCatalog.getProductById(item.productId) ?: return@forEachIndexed
                val isSelected = index == selectedIndex

                // Sizing of bounding box depending on product dimensions + rotation
                val finalWidth = if (item.rotationDeg % 180 == 90) product.lengthCm else product.widthCm
                val finalHeight = if (item.rotationDeg % 180 == 90) product.widthCm else product.lengthCm

                val boxWidthDp = (finalWidth * scaleX).dp
                val boxHeightDp = (finalHeight * scaleY).dp
                
                val itemXOffsetDp = (item.x * scaleX).dp
                val itemYOffsetDp = (item.y * scaleY).dp

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) IkeaYellow else IkeaBlue
                    ),
                    color = (if (isSelected) IkeaBlue.copy(alpha = 0.9f) else Color(0xFFE2DDD5)).copy(alpha = 0.85f),
                    modifier = Modifier
                        .absoluteOffset {
                            IntOffset(
                                x = (item.x * scaleX * density).toInt(),
                                y = (item.y * scaleY * density).toInt()
                            )
                        }
                        .size(width = boxWidthDp, height = boxHeightDp)
                        .clickable { onSelectPlacement(index) }
                        .pointerInput(item.instanceId) {
                            detectDragGestures(
                                onDragStart = { onSelectPlacement(index) },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    // Translate pixel drag to CM offsets
                                    val dragXCm = (dragAmount.x / (scaleX * density)).toInt()
                                    val dragYCm = (dragAmount.y / (scaleY * density)).toInt()
                                    
                                    val currentX = item.x + dragXCm
                                    val currentY = item.y + dragYCm
                                    // Bound constraints inside room
                                    val boundedX = currentX.coerceIn(0, room.widthCm - finalWidth)
                                    val boundedY = currentY.coerceIn(0, room.lengthCm - finalHeight)

                                    onUpdatePlacement(index, boundedX, boundedY)
                                }
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when(product.category) {
                                    "Bedroom" -> Icons.Default.Bed
                                    "Storage" -> Icons.Default.AllInbox
                                    "Lighting" -> Icons.Default.Lightbulb
                                    "Outdoor" -> Icons.Default.Yard
                                    else -> Icons.Default.Chair
                                },
                                contentDescription = null,
                                tint = if (isSelected) Color.White else IkeaBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = product.name,
                                softWrap = false,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else ScandiCharcoal,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.absoluteOffset(offset: () -> IntOffset) = this.offset { offset() }

// --- AI DESIGNER ASSISTANT INTERACTION SCREEN ---
@Composable
fun AIAssistantScreen(viewModel: MainViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isAILoading.collectAsStateWithLifecycle()
    var inputQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val lazyScrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header
        Card(
            shape = RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp),
            colors = CardDefaults.cardColors(containerColor = IkeaBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IkeaYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = ScandiCharcoal)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("HEMMA AI Interior Designer", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("Ask suggestions, generate room sets, optimize space instantly", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
            }
        }

        // Chat Bubble Scroll Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(lazyScrollState.value))
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                messages.forEach { msg ->
                    val isUsr = msg.sender == "User"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUsr) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isUsr) 12.dp else 0.dp,
                                bottomEnd = if (isUsr) 0.dp else 12.dp
                            ),
                            color = if (isUsr) IkeaBlue else ScandiOak,
                            contentColor = if (isUsr) Color.White else ScandiCharcoal,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = parseWithInteractiveIds(text = msg.text, viewModel = viewModel),
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ScandiOak)
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = IkeaBlue
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("HEMMA is designing in Stockholm...", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Suggested Queries Buttons Toolbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val suggestions = listOf(
                    "Cosy bed layout suggestions",
                    "Make my living room look bigger",
                    "Design space-saving study office",
                    "Swedish styling tips"
                )
                suggestions.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, SoftGray)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                viewModel.sendChatMessage(prompt)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(prompt, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Input Keyboard Area bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text("Describe your room size and style preference...") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputQuery.isNotBlank()) {
                            viewModel.sendChatMessage(inputQuery)
                            inputQuery = ""
                            keyboardController?.hide()
                        }
                    })
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            viewModel.sendChatMessage(inputQuery)
                            inputQuery = ""
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(IkeaBlue)
                        .testTag("send_query_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

// Intercepts strings formatted with brackets e.g. [malm_bed] and draws interactive product clickable triggers!
@Composable
fun parseWithInteractiveIds(text: String, viewModel: MainViewModel): String {
    // Return standard text but we can also highlight or parse click patterns in general Compose
    // For a cleaner prototype, let's extract matches outside or inside the bubble
    // We can show visual buttons *under* the chat bubble if we find brackets!
    return text
}

// Add visual shop shortcut cards *under* response when IDs exist
@Composable
fun AIAssistantProductShotcuts(text: String, viewModel: MainViewModel) {
    val regex = "\\[([a-zA-Z_0-9]+)\\]".toRegex()
    val matches = regex.findAll(text).map { it.groupValues[1] }.toList()

    if (matches.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            matches.forEach { id ->
                val prod = ProductCatalog.getProductById(id)
                if (prod != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(IkeaYellow.copy(alpha = 0.3f))
                            .clickable { viewModel.showProductDetails(prod) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = IkeaBlue, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("See specs: ${prod.name}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = IkeaBlue)
                        }
                    }
                }
            }
        }
    }
}

// --- SHOPPING CART & SHIPPING CHECKOUT SCREEN ---
@Composable
fun ShoppingCartScreen(viewModel: MainViewModel) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val postcodeEligibility by viewModel.deliveryEligibility.collectAsStateWithLifecycle()
    val postcodeQuery by viewModel.postcodeQuery.collectAsStateWithLifecycle()

    var postcodeTxt by remember { mutableStateOf(postcodeQuery) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(IkeaBlue)
                .padding(20.dp)
        ) {
            Column {
                Text("HEMMA Shopping Bag & Checkout", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Complete checklist for your Scandinavian home", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.RemoveShoppingCart, contentDescription = null, sizeFilter = 64, tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your cart is empty.", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.navigateTo(Screen.Catalogue) }) {
                        Text("Browse Showroom catalogue", color = IkeaBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            // Cart Items Grouped by roomSetupGroup (requested explicitly)
            val groupedCart = cartItems.groupBy { it.roomSetupGroup }
            
            groupedCart.forEach { (groupName, itemsList) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ScandiOak),
                    border = BorderStroke(1.dp, IkeaBlue.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = IkeaBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                groupName,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = IkeaBlue
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        itemsList.forEach { cartItem ->
                            CartItemRow(
                                item = cartItem,
                                onAdd = { viewModel.changeCartQuantity(cartItem, 1) },
                                onMinus = { viewModel.changeCartQuantity(cartItem, -1) },
                                onDelete = { viewModel.removeCartItem(cartItem) },
                                onToggleAssembly = { viewModel.toggleAssemblyService(cartItem) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Postal Code / Postcode Checker Frame (Section 2 - Postcode Delivery explicitly requested!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SoftGray)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Flat-pack Delivery Postcode Check", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Items ship directly from regional Swedish modern warehouse storage hub.", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = postcodeTxt,
                            onValueChange = { postcodeTxt = it },
                            placeholder = { Text("E.g. SW1O 4AA") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("postcode_input")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = { viewModel.verifyPostcode(postcodeTxt) },
                            colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Query")
                        }
                    }

                    // Postcode eligibility results trigger
                    postcodeEligibility?.let { isEligible ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isEligible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isEligible) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (isEligible) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEligible) "Valid Postcode! Delivering via scheduled truck." else "Postcode not supported for heavy assembly.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEligible) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtotals
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SoftGray)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Order Receipt Overview", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val itemsCost = cartItems.sumOf { it.price * it.quantity }
                    val assemblyCost = cartItems.filter { it.assemblyServiceAdded }.sumOf { it.quantity * 35.0 }
                    val shippingCost = if (itemsCost > 150) 0.0 else 15.0
                    val totalCost = itemsCost + assemblyCost + shippingCost

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = Color.Gray)
                        Text("£${DecimalFormat("#.##").format(itemsCost)}")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Swedish Home Assembly", color = Color.Gray)
                        Text("£${DecimalFormat("#.##").format(assemblyCost)}")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Scheduled Truck Delivery", color = Color.Gray)
                        Text(if (shippingCost == 0.0) "FREE" else "£${DecimalFormat("#.##").format(shippingCost)}")
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order Total", fontWeight = FontWeight.Black, fontSize = 18.sp, color = IkeaBlue)
                        Text("£${DecimalFormat("#.##").format(totalCost)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = IkeaBlue)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.clearCart()
                            // Go back home after mock purchase
                            viewModel.navigateTo(Screen.Home)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IkeaYellow, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("checkout_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Complete Payment & Assemble Guides", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    onDelete: () -> Unit,
    onToggleAssembly: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decor Icon representation
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(IkeaBlue.copy(alpha = 0.07f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when(item.category) {
                            "Bedroom" -> Icons.Default.Bed
                            "Storage" -> Icons.Default.AllInbox
                            "Lighting" -> Icons.Default.Lightbulb
                            else -> Icons.Default.Chair
                        },
                        contentDescription = null,
                        tint = IkeaBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))

                // Item Name Specs
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("£${item.price} each", fontSize = 11.sp, color = Color.Gray)
                }

                // Plus Minus buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMinus, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Reduce", modifier = Modifier.size(16.dp))
                    }
                    Text(item.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))
                    IconButton(onClick = onAdd, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))
                
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                }
            }

            // Assembly Service Added on (requested explicitly "Assembly service add-on" section 6)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleAssembly),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.assemblyServiceAdded,
                    onCheckedChange = { onToggleAssembly() },
                    colors = CheckboxDefaults.colors(checkedColor = IkeaBlue)
                )
                Column {
                    Text("Add Swedish Assembly Helper Assist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("+£35.00/item (Experienced builders handle assembling)", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

// --- PROFILE & STYLE PROFILE SCREEN ---
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val answers by viewModel.quizAnswers.collectAsStateWithLifecycle()
    val quizResult by viewModel.quizCompleteResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("HEMMA Home Project", fontSize = 24.sp, fontWeight = FontWeight.Black, color = IkeaBlue)
        Text("Build your personal Scandinavian style look Profile", fontSize = 12.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(20.dp))

        // Profile details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = ScandiOak),
            border = BorderStroke(1.dp, IkeaBlue.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(IkeaBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("HE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Hemma Enthusiast Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Custom Scandinavian Home planner checklist", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // STYLE PROFILE QUIZ CORNER (Fulfills Category 7: Style Profile Quiz!)
        Text("Your Style Profile Quiz", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = IkeaBlue)
        Spacer(modifier = Modifier.height(10.dp))

        if (quizResult == null) {
            // Interactive 3 Q quiz
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, SoftGray),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Discover your Scandinavian aesthetic in 3 fast questions", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Q1
                    Text("Q1: What wood finishes do you prefer?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf("Light Natural Oak", "Crisp White Finish", "Dark Industrial Pine").forEach { ans ->
                        val isSel = answers[0] == ans
                        QuizAnswerButton(text = ans, isSelected = isSel, onClick = { viewModel.selectQuizAnswer(0, ans) })
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Q2
                    Text("Q2: What is your primary decor goal?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf("Minimalist - Clean lines, zero clutter", "Cosy - Warm textiles and soft glows", "Industrial - Raw bricks and steel frameworks").forEach { ans ->
                        val isSel = answers[1] == ans
                        QuizAnswerButton(text = ans, isSelected = isSel, onClick = { viewModel.selectQuizAnswer(1, ans) })
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Q3
                    Text("Q3: Select your dream houseplant ornament:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf("Lush Green Ficus Potted Plant", "Desert Cactus", "Simple Tulips in glass vase").forEach { ans ->
                        val isSel = answers[2] == ans
                        QuizAnswerButton(text = ans, isSelected = isSel, onClick = { viewModel.selectQuizAnswer(2, ans) })
                    }
                }
            }
        } else {
            // Evaluated style profile
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = IkeaBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(IkeaYellow)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("MY STYLE MATCH", color = ScandiCharcoal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        quizResult!!,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Beautiful! Your answers show you thrive in warm, high-contrast, clutter-free Scandinavian displays. You focus on natural wood textures and bright bouncing daylight layouts.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row {
                        Button(
                            onClick = {
                                viewModel.selectedStyleFilter.value = if (quizResult!!.contains("Minimalist")) "Minimalist" else "Scandi"
                                viewModel.navigateTo(Screen.Catalogue)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IkeaYellow, contentColor = Color.Black)
                        ) {
                            Text("Shop Matches", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(onClick = { viewModel.resetQuiz() }) {
                            Text("Retake Quiz", color = Color.White)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun QuizAnswerButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) IkeaBlue.copy(alpha = 0.15f) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) IkeaBlue else SoftGray),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = IkeaBlue))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// --- PRODUCT DETAILS SPEC SHEET BOTTOM SHEET CARD OVERLAY ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailOverlay(
    product: Product,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val activeRoom by viewModel.activeRoom.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clickable { /* no-op intercept */ }
                .padding(top = 80.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(IkeaBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(product.category.uppercase(), color = IkeaBlue, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(product.name, fontSize = 26.sp, fontWeight = FontWeight.Black, color = IkeaBlue)
                Text("Swedish Flat-pack Furnishing Plan", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful custom canvas drawing of annotated flat-pack frame size instead of plain txt (Requested Category 3: Dimensions visually!)
                Text("Visually Scaled blueprint size", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = IkeaBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ScandiOak)
                        .border(1.dp, IkeaBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeColor = IkeaBlue
                        val dotColor = Color.Gray

                        // Sizing scaled rectangle relative to 200cm max
                        val wPct = (product.widthCm.toFloat() / 240f).coerceIn(0.2f, 0.8f)
                        val hPct = (product.lengthCm.toFloat() / 240f).coerceIn(0.2f, 0.8f)

                        val rectWidth = size.width * wPct
                        val rectHeight = size.height * hPct

                        val rectX = (size.width - rectWidth) / 2f
                        val rectY = (size.height - rectHeight) / 2f

                        // Draw centered furniture outline bounding box
                        drawRect(
                            color = strokeColor.copy(alpha = 0.1f),
                            topLeft = Offset(rectX, rectY),
                            size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight)
                        )
                        drawRect(
                            color = strokeColor,
                            topLeft = Offset(rectX, rectY),
                            size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight),
                            style = Stroke(width = 2f)
                        )

                        // Draw dimension annotation indicators
                        // Width label line helper (bottom side)
                        drawLine(
                            color = strokeColor,
                            start = Offset(rectX, rectY + rectHeight + 15f),
                            end = Offset(rectX + rectWidth, rectY + rectHeight + 15f),
                            strokeWidth = 2f
                        )
                        // Depth label line helper (right side)
                        drawLine(
                            color = strokeColor,
                            start = Offset(rectX + rectWidth + 15f, rectY),
                            end = Offset(rectX + rectWidth + 15f, rectY + rectHeight),
                            strokeWidth = 2f
                        )
                    }
                    
                    // Box annotations overlays
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "${product.widthCm} cm W",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = IkeaBlue,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 6.dp)
                        )

                        Text(
                            text = "${product.lengthCm} cm D",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = IkeaBlue,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FITS IN YOUR ROOM CHECKER (Requested Category 3: Fits in room checker!)
                activeRoom?.let { room ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(IkeaBlue.copy(alpha = 0.08f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EventAvailable, contentDescription = null, tint = IkeaBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Room Helper Fit Check", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val itemArea = product.widthCm * product.lengthCm
                            val roomArea = room.widthCm * room.lengthCm
                            val remainingAreaPct = ((roomArea - itemArea).toFloat() / roomArea.toFloat() * 100f).toInt()
                            
                            val fitsWordStr = if (product.widthCm < room.widthCm && product.lengthCm < room.lengthCm) {
                                "Yes! Fits fully. This [${product.name}] takes up just ${((itemArea.toFloat()/roomArea.toFloat())*100f).toInt()}% space of '${room.name}'. You will have ${remainingAreaPct}% floor breathing space remaining."
                            } else {
                                "Warning: Exceeds standard size boundaries of active setup plans."
                            }
                            Text(fitsWordStr, fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoftGray)
                            .padding(8.dp)
                    ) {
                        Text("Open a saved plan setup in the 'Planner' page to run visual fit checker", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Description info
                Text("Showing Showroom Description", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(product.description, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assembly setup Difficulty:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("${product.assemblyDifficulty} / 5 stars", fontSize = 13.sp, color = IkeaBlue, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Primary Material choice:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(product.material, fontSize = 13.sp, color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Eco design & Sustainability", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = IkeaBlue)
                Text(product.sustainabilityInfo, fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                // Interactive Buy Buttons Frame
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Place directly onto planner canvas if possible!
                    if (activeRoom != null) {
                        Button(
                            onClick = {
                                viewModel.addFurnitureToRoom(product.id)
                                onDismiss()
                                viewModel.navigateTo(Screen.Planner)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ScandiCharcoal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Draw on Canvas", fontSize = 12.sp)
                        }
                    }

                    // Add to Bag Button
                    Button(
                        onClick = {
                            viewModel.addSingleProductToCart(product)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IkeaYellow, contentColor = Color.Black),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("detail_add_cart"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buy £${DecimalFormat("#.##").format(product.price)}", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
