package com.card.infoshelf.bottomfragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {


    DatabaseReference Ref, newRef;
    private String CurrentUserId, userId;
    private FirebaseAuth mAuth;
    TextInputLayout currComp,JobRole,JobExp;
    TextInputEditText user_name, user_email, user_bio, user_college, user_course, user_profession,user_currentCom,user_jobRole,user_jobEx;
    Button savechange;
    Boolean isvalidation = false;
    TextView errztext;

    private ArrayList<String> saoi,Daoi;
    private LinearLayout l_saoi,clgfield,coursefield,l_dream;
    private ImageView saoi_add,dream_add;
    private AutoCompleteTextView s_area_of_interest, s_dream_company, g_area_of_interest, g_dream_company, j_company_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();


        Ref = FirebaseDatabase.getInstance().getReference();

        user_name = findViewById(R.id.u_Name);
        user_email = findViewById(R.id.u_Email);
        user_profession = findViewById(R.id.u_Profession);
        user_bio = findViewById(R.id.u_bio);
        user_college = findViewById(R.id.u_College);
        user_course = findViewById(R.id.u_Course);
        savechange = findViewById(R.id.saveChanges);
        clgfield = findViewById(R.id.collegefieldLayout);
        coursefield = findViewById(R.id.CoursefieldLayout);
        errztext = findViewById(R.id.errortxt);
        user_currentCom = findViewById(R.id.u_current_company);
        user_jobEx = findViewById(R.id.u_job_ex);
        user_jobRole = findViewById(R.id.u_job_rol);
        s_dream_company = findViewById(R.id.s_dream_company);
        s_area_of_interest = findViewById(R.id.s_area_of_interest);
        user_jobRole = findViewById(R.id.u_job_rol);

        currComp = findViewById(R.id.currCom);
        JobExp = findViewById(R.id.jobExp);
        JobRole = findViewById(R.id.jobRole);

        s_dream_company = findViewById(R.id.s_dream_company);
        dream_add = findViewById(R.id.dream_add);
        saoi_add = findViewById(R.id.saoi_add);
        l_dream = findViewById(R.id.l_dream);
        l_saoi = findViewById(R.id.l_saoi);

        saoi = new ArrayList<>();
        Daoi = new ArrayList<>();

        // checck profession graduation schooing job
        Ref.child("Users").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GridModel gridModel = snapshot.getValue(GridModel.class);
                user_name.setText(gridModel.getUserName());
                user_email.setText(gridModel.getUserEmail());

                Ref.child("UserDetails").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GridModel gridModel = snapshot.getValue(GridModel.class);
                        if (snapshot.child("user_bio").exists()) {
                            user_bio.setText(gridModel.getUser_bio());
                        }
                        if (snapshot.child("profession").getValue().equals("Graduation")) {
                            user_profession.setText(gridModel.getProfession());
                            user_college.setText(gridModel.getCollege_name());
                            user_course.setText(gridModel.getCourse());
                            currComp.setVisibility(View.GONE);
                            JobExp.setVisibility(View.GONE);
                            JobRole.setVisibility(View.GONE);

                        } else if (snapshot.child("profession").getValue().equals("Schooling")) {
                            user_profession.setText(gridModel.getProfession());
                            user_college.setText(gridModel.getSchool_name());
                            user_course.setText(gridModel.getStandard());
                            currComp.setVisibility(View.GONE);
                            JobExp.setVisibility(View.GONE);
                            JobRole.setVisibility(View.GONE);


                        } else {
                            user_profession.setText(gridModel.getProfession());
                            user_college.setText(gridModel.getPast_clg_name());
                            user_course.setText(gridModel.getQualification());
                            currComp.setVisibility(View.VISIBLE);
                            JobExp.setVisibility(View.VISIBLE);
                            JobRole.setVisibility(View.VISIBLE);

                            user_currentCom.setText(gridModel.getCurrent_company());
                            user_jobEx.setText(gridModel.getJob_ex());
                            user_jobRole.setText(gridModel.getJob_role());

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // getting interest area node data in to an arraylist
        Ref.child("UserDetails").child(CurrentUserId).child("AreaOfInterest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                    saoi.add(snapshot.child(i + "").getValue().toString());
                }
                printAreaOFInterest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Ref.child("UserDetails").child(CurrentUserId).child("CompanyName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                for (int i = 0; i < snapshot1.getChildrenCount(); i++) {
                    Daoi.add(snapshot1.child(i+"").getValue().toString());
                }
                printDreamOFInterest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        savechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                if (isvalidation == true) {
                    sendChangedDataFirebase();
                }

            }
        });

        String[] ArrayInterest = {"Effective communication", "Teamwork", "Responsibility", "Creativity", "Problem-solving", "Leadership", "Extroversion", "People skills", "Openness", "Adaptability", "Data analysis", "Web analytics", "Wordpress", "Email marketing", "Web scraping", "CRO and A/B Testing", "Data visualization & pattern-finding through critical thinking", "Search Engine and Keyword Optimization", "Project/campaign management", "Social media and mobile marketing", "Paid social media advertisements", "B2B Marketing", "The 4 P-s of Marketing", "Consumer Behavior Drivers", "Brand management", "Creativity", "Copywriting", "Storytelling", "Sales", "CMS Tools", "Six Sigma techniques", "The McKinsey 7s Framework", "Porter’s Five Forces", "PESTEL", "Emotional Intelligence", "Dealing with work-related stress", "Motivation", "Task delegation", "Technological savviness", "People management", "Business Development", "Strategic Management", "Negotiation", "Planning", "Proposal writing", "Problem-solving", "Innovation", "Charisma", "Algorithms", "Analytical Skills", "Big Data", "Calculating", "Compiling Statistics", "Data Analytics", "Data Mining", "Database Design", "Database Management", "Documentation", "Modeling", "Modification", "Needs Analysis", "Quantitative Research", "Quantitative Reports", "Statistical Analysis", "HTML", "Implementation", "Information Technology", "ICT (Information and Communications Technology)", "Infrastructure", "Languages", "Maintenance", "Network Architecture", "Network Security", "Networking", "New Technologies", "Operating Systems", "Programming", "Restoration", "Security", "Servers", "Software", "Solution Delivery", "Storage", "Structures", "Systems Analysis", "Technical Support", "Technology", "Testing", "Tools", "Training", "Troubleshooting", "Usability", "Benchmarking", "Budget Planning", "Engineering", "Fabrication", "Following Specifications", "Operations", "Performance Review", "Project Planning", "Quality Assurance", "Quality Control", "Scheduling", "Task Delegation", "Task Management", "Content Management Systems (CMS)", "Blogging", "Digital Photography", "Digital Media", "Networking", "Search Engine Optimization (SEO)", "Social Media Platforms (Twitter, Facebook, Instagram, LinkedIn, TikTok, Medium, etc.)", "Web Analytics", "Automated Marketing Software", "Client Relations", "Email", "Requirements Gathering", "Research", "Subject Matter Experts (SMEs)", "Technical Documentation", "Information Security", "Microsoft Office Certifications", "Video Creation", "Customer Relationship Management (CRM)", "Productivity Software", "Cloud/SaaS Services", "Database Management", "Telecommunications", "Human Resources Software", "Accounting Software", "Enterprise Resource Planning (ERP) Software", "Database Software", "Query Software", "Blueprint Design", "Medical Billing", "Medical Coding", "Sonography", "Structural Analysis", "Artificial Intelligence (AI)", "Mechanical Maintenance", "Manufacturing", "Inventory Management", "Numeracy", "Information Management", "Hardware Verification Tools and Techniques", "PHP", "TypeScript", "Scala", "Shell", "PowerShell", "Perl", "Haskell", "Kotlin", "Visual Basic .NET", "SQL", "Delphi", "MATLAB", "Groovy", "Lua", "Rust", "Ruby", "HTML and CSS", "Python", "Java", "JavaScript", "Swift", "C++", "C#", "R", "Golang (Go)", "Soccer", "Football", "Cycling", "Running", "Basketball", "Swimming", "Tennis", "Baseball", "Yoga", "Hiking", "Camping", "Fishing", "Trekking", "Mountain climbing", "Gardening", "Drawing", "Painting", "Watercoloring", "Sculpture", "Woodworking", "Dance", "graphics designing", "Front-End development", "Back-End development", "Content Writting", "essay writting", "Event organising", "Hackathon", "Bookkeeping", "Graphic design", "Data analysis", "Microsoft Excel", "Public speaking", "Budgeting", "Teaching", "Research", "Microsoft Word", "Scheduling", "Sales", "Project management", "Office management", "Fundraising", "Writing", "Editing", "Event promotion", "Event planning", "Bilingual", "Management experience", "Communication skills (both written and oral)", "Customer service", "Problem-solving", "Organizational skills", "Inventive", "Handling conflict", "Listening", "Attention to detail", "Collaboration", "Curious", "Diplomacy", "Friendly", "Flexible", "Responsible", "Punctual", "Reliable", "Takes initiative", "Persistent", "Leadership", "Enthusiastic", "Android Studio", "Android Development", "Web Development", "Machine Learning", "Artificial intelligence", "Robots", "Augmented reality", "virtual reality", "Cryptography", "Hacking", "Xml"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(EditProfile.this, android.R.layout.select_dialog_item, ArrayInterest);
        s_area_of_interest.setAdapter(adapter1);

        String[] ArrayCompany = {"Reliance Industries", "Indian Oil Corporation", "Oil  Natural Gas Corporation", "State Bank of India", "Bharat Petroleum Corporation", "Tata Motors", "Rajesh Exports", "Tata Consultancy Services", "ICICI Bank", "Larsen  Toubro", "HDFC Bank", "Tata Steel", "Hindalco Industries", "NTPC", "HDFC", "Coal India", "Mahindra  Mahindra", "Infosys", "Bank of Baroda", "Bharti Airtel", "Nayara Energy", "Vedanta", "Axis Bank", "Grasim Industries", "Maruti Suzuki India", "GAIL (India)", "JSW Steel", "HCL Technologies", "Steel Authority of India", "Punjab National Bank", "Motherson Sumi Systems", "Wipro", "Power Finance Corporation", "Canara Bank", "Bajaj Finserv", "ITC", "General Insurance Corporation of India", "Kotak Mahindra Bank", "Bank of India", "Vodafone Idea", "Jindal Steel  Power", "Union Bank of India", "UltraTech Cement", "Power Grid Corporation of India", "Hindustan Unilever", "Tech Mahindra", "Yes Bank", "InterGlobe Aviation", "UPL", "IndusInd Bank", "Sun Pharmaceuticals Industries", "NABARD", "Bajaj Auto", "Tata Power Company", "Hero MotoCorp", "New India Assurance Company", "IFFCO", "Adani Power", "Ambuja Cements", "Central Bank of India", "IDBI Bank", "Avenue Supermarts", "Indian Bank", "Aurobindo Pharma", "Reliance Capital", "Bharat Heavy Electricals", "Reliance Infrastructure", "Titan Company", "Hindustan Aeronautics", "Ashok Leyland", "Asian Paints", "Indian Overseas Bank", "Future Retail", "Sundaram Clayton", "LIC Housing Finance", "TVS Motor Company", "Max Financial Services", "Dr. Reddys Laboratories", "PTC India", "UCO Bank", "IDFC First Bank", "Cipla", "Citibank", "Tata Chemicals", "Tata Communications", "Aditya Birla Capital", "EID Parry (India)", "Shriram Transport Finance Company", "MRF", "Apollo Tyres", "Toyota Kirloskar Motor", "Lupin", "ACC", "Federal Bank", "Piramal Enterprises", "Rail Vikas Nigam", "Exide Industries", "Oil India", "Standard Chartered Bank", "Cadila Healthcare", "Siemens", "Torrent Power", "HSBC", "Adani Ports and Special Economic Zone", "Indiabulls Housing Finance", "SpiceJet", "SIDBI", "National Fertilizer", "Jindal Stainless", "Bank of Maharashtra", "Shree Cement", "Bharat Electronics", "Kalpataru Power Transmission", "Nestle India", "Macrotech Developers", "NMDC", "Bandhan Bank", "Gujarat State Petronet", "Chambal Fertilisers  Chemicals", "Bombay Burmah Trading Corporation", "Godrej Industries", "KEC International", "MM Financial Services", "CESC", "Britannia Industries", "NLC India", "Adani Transmission", "godrej  Boyce Manufacturing Company", "Apollo Hospitals Enterprise", "Jindal Saw", "Adani Enterprises", "Varroc Engineering", "Welspun Corp", "Jaiprakash Associates", "NHPC", "Rain Industries", "Glenmark Pharmaceuticals", "RBL Bank", "Bosch", "Godrej Consumer Products", "Afcons Infrastructure", "Hindustan Construction Company", "Dalmia Bharat", "Rashtriya Chemicals  Fertilizers", "Tata Capital", "Tata Consumer Products", "Dilip Buildcon", "Muthoot Finance", "Eicher Motors", "Edelweiss Financial Services", "Dewan Housing Finance Corporation", "Jubilant Life Sciences", "Havells India", "United Spirits", "GMR Infrastructure", "JSW Energy", "Aditya Birla Fashion  Retail", "Polycab India", "National Aluminium Company", "Dabur India", "Mphasis", "Aster DM Healthcare", "NCC", "jammu and Kashmir Bank", "Punjab  Sind Bank", "South Indian Bank", "JK Tyre  Industries", "Cholamandalam Investment  Finance Co.", "Varun Beverages", "Alkem Laboratories", "Export-Import Bank of India", "Bharti Infratel", "Security  Intelligence Services (India)", "DCM Shriram", "NBCC (India)", "Reliance Power", "Torrent Pharmaceuticals", "Bharat Forge", "Voltas", "marico", "APL Apollo Tubes", "Karnataka Bank", "Mahindra CIE Automotive", "BASF India", "Shree Renuka Sugars", "GSFC", "Whirlpool of India", "Apar Industries", "HUDCO", "Uflex", "Allcargo Logistics", "SRF", "ABB India", "Pidilite Industries", "Deutsche Bank", "Arvind", "DLF", "Aegis Logistics", "Karur Vysya Bank", "IRB Infrastructure Developers", "Birla Corporation", "Raymond", "Vardhman Textiles", "Welspun India", "Endurance Technologies", "Thomas Cook (India)", "Container Corporation Of India", "Amara Raja Batteries", "CEAT", "Biocon", "Prestige Estates Projects", "Bajaj Hindusthan Sugar", "United Breweries", "Berger Paints India", "Future Lifestyle Fashions", "Jain Irrigation Systems", "Shriram City Union Finance", "Prism Johnson", "SREI Infrastructure Finance", "ISGEC Heavy Engineering", "JK Cement", "Escorts", "Sterling  Wilson Solar", "Thermax", "Fullerton India Credit Company", "PNC Infratech", "Supreme Industries", "PC Jeweller", "Divis Laboratories", "Hexaware Technologies", "Surya Roshni", "Mazagon Dock Shipbuilders", "Ircon International", "Manappuram Finance", "Cummins India", "Minda Industries", "Nirma", "The Ramco Cements", "Future Enterprises", "Blue Star", "Zee Entertainment Enterprises", "Sadbhav Engineering", "GNFC", "National Housing Bank", "Kansai Nerolac Paints", "The India Cements", "Hatsun Agro Product", "Team Lease Services", "Ashoka Buildcon", "CG Power  Industrial Solutions", "Sterlite Technologies", "Hinduja Global Solutions", "Oracle Financial Services Software", "KEI Industries", "AGC Networks", "Balkrishna Industries", "AU Small Finance Bank", "Bajaj Electricals", "Walmart India", "Fortis Healthcare", "Ipca Laboratories", "City Union Bank", "Alembic Pharmaceuticals", "IIFL Finance", "Trident", "India Infrastructure Finance Company", "Balrampur Chini Mills", "Sundaram Finance", "Bank of America", "Crompton Greaves Consumer Electrical", "Shipping Corporation of India", "Tube Investments of India", "Indian Hotels Co", "RattanIndia Power", "Deepak Fertilisers  Petrochemicals Corp", "Polyplex Corporation", "Cyient", "Colgate-Palmolive (India)", "JK Lakshmi Cement", "Dixon Technologies (India)", "Schaeffler India", "KRBL", "Triveni Engineering and Industries", "Zensar Technologies", "Coforge", "Deepak Nitrite", "Avanti Feeds", "Aarti Industries", "Atul", "Sobha", "Zuari Agro Chemicals", "Abbott India", "Firstsource Solutions", "Simplex Infrastructures", "LT Foods", "Future Consumer", "Jubilant Foodworks", "Amber Enterprises India", "Tamilnad Mercantile Bank", "Tamil Nadu Newsprint  Papers", "HFCL", "Great Eastern Shipping Company", "DCB Bank", "GSK Pharmaceuticals", "Castrol India", "Gujarat Ambuja Exports", "Sonata Software", "Sun TV Network", "The Fertilizers  Chemicals Travancore", "Gokul Agro Resources", "Trent", "Persistent Systems", "Sundram Fasteners", "Cochin Shipyard", "Shoppers Stop", "Jindal Poly Films", "Jayaswal Neco Industries", "Gayatri Projects", "Bayer CropScience", "Time Technoplast", "Dish TV India", "JK Paper", "KPR Mill", "Jaiprakash Power Ventures", "Kirloskar Oil Engines", "Century Textiles  Industries", "Engineers India", "India Glycols", "Dhampur Sugar Mills", "Oberoi Realty", "JM Financial", "PVR", "PI Industries", "Honeywell Automation India", "BEML", "Birlasoft", "SJVN", "GHCL", "IFB Industries", "Phillips Carbon Black", "Electrotherm (India)", "Godawari Power  Ispat", "GE TD India", "Venkys (India)", "Blue Dart Express", "Graphite India", "Kirloskar Brothers", "Sanofi India", "Bata India", "Narayana Hrudayalaya", "Finolex Industries", "Brigade Enterprises", "Godfrey Phillips India", "AIA Engineering", "Laurus Labs", "Finolex Cables", "PG Hygiene and Health Care", "Heritage Foods", "Force Motors", "Mukand", "Prime Focus", "J Kumar Infraprojects", "Jai Balaji Industries", "Prakash Industries", "Ujjivan Financial Services", "SKF India", "Strides Pharma Science", "Page Industries", "Montecarlo", "Equitas Holdings", "Indiabulls Real Estate", "Dhani Services", "Kajaria Ceramics", "Forbes  Company", "IFCI", "Wockhardt", "ITD Cementation India", "Nava Bharat Ventures", "Garden Silk Mills", "Redington India", "Electrosteel Castings", "Godrej Properties", "JBF Industries", "Filatex India", "RSWM", "Patel Engineering", "EPL", "Gujarat Alkalies  Chemicals", "G4S Secure Solutions (India)", "Minda Corporation", "Gland Pharma", "Transport Corporation of India", "Konkan Railway Corporation", "BGR Energy Systems", "Akzo Nobel India", "Supreme Petrochem", "Granules India", "Emami", "Bharat Dynamics", "Brightcom Group", "Asahi India Glass", "Ratnamani Metals  Tubes", "Maharashtra Seamless", "Ajanta Pharma", "Shankara Building Products", "Carborundum Universal", "Kesoram Industries", "GFL", "Astral Poly Technik", "Orient Cement", "Rites", "West Coast Paper Mills", "Wheels India", "V-Guard Industries", "Usha Martin", "Parag Milk Foods", "Linde India", "Avadh Sugar  Energy", "Va Tech Wabag", "HIL", "Huhtamaki PPL", "GE Power India", "Canon India", "Galaxy Surfactants", "Magma Fincorp", "Lakshmi Vilas Bank", "Suzlon Energy", "KNR Constructions", "Denso Haryana", "Jayant Agro Organics", "Relaxo Footwears", "MEP Infrastructure Developers", "Radico Khaitan", "Sutlej Textiles and Industries", "BNP Paribas", "Siyaram Silk Mills", "Jana Small Finance Bank", "Himatsingka Seide", "Johnson Controls - Hitachi Air Condition. India", "Religare Enterprises", "3M India", "Dalmia Bharat Sugar  Industries", "Nectar Lifescience", "Pfizer", "Transcorp International", "Motilal Oswal Financial Services", "IRCTC", "Hindusthan National Glass  Industries", "HT Media", "Solar Industries India", "NIIT", "Sumitomo Chemical India", "Century Plyboards (India)", "Sify Technologies", "Nilkamal", "Vindhya Telelinks", "Meghmani Organics", "Cosmo Films", "Take Solutions", "DB Corp", "H.G. Infra Engineering", "HEG", "Rane Holdings", "HeidelbergCement India", "Sheela Foam", "Indo Rama Synthetics (India)", "Jindal Worldwide", "Orient Electric", "Power Mech Projects", "KPIT Technologies", "American Express Bank", "Pennar Industries", "Jagran Prakashan", "VRL Logistics", "TVS Srichakra", "Dishman Carbogen Amcis", "Indo Count Industries", "Natco Pharma", "TTK Prestige", "Southern Petrochemical Industries Corporation", "Subros", "KIOCL", "Savita Oil Technologies", "Nahar Spinning Mills", "Sharda Cropchem", "Gokul Refoils and Solvent", "Sarda Energy  Minerals", "HSIL", "Max Healthcare Institute", "Greaves Cotton", "WABCO India", "Tanla Platforms", "Bombay Dyeing  Manufacturing Company", "Sandhar Technologies", "Texmaco Rail  Engineering", "Phoenix Mills", "Kirloskar Industries", "Bilcare", "Ahluwalia Contracts (India)", "IOL Chemicals  Pharmaceuticals", "Flipkart", "Vmware", "Nutanix", "Oyo rooms", "Goibibo", "MakeMyTrip", "Wow! Momo", "Ola Cabs", "AddressHealth", "Zomato", "One97 (Paytm)", "FreshToHome", "‍FreshMenu", "Flyrobe", "Myra", "Cure.Fit", "Dunzo", "Shuttl", "Digit Insurance", "CoolBerg", "Cleardekho", "The Minimalist", "Razorpay", "Nineleaps", "Innov8 Coworking", "Schbang", "Acko General Insurance", "Treebo Hotels", "InCred", "Jumbotail", "DocTalk", "Smallcase", "Vedantu", "Instavans", "Loan Frame", "Overcart", "Flock", "Doctor Insta", "Cowrks", "1Mg", "Cars24", "Dailyhunt", "Ebutor", "Meesho", "MilkBasket", "PharmEasy", "Policybazaar", "Revv", "Sharechat", "Nykaa", "Toppr", "TravelTriangle", "Urban Ladder", "Aisle", "Me", "Bombay Shaving Company", "POPxo", "Zestmoney", "Xpressbees", "Swiggy", "Boeing", "Snapdeal", "Jio", "Arcesium", "Amazon", "Microsoft", "Apple", "Netflix", "Facebook", "Snapdeal", "Ixigo", "Walmart", "Hashedin", "Linkedin", "Zomato", "Swiggy", "Uber", "Urban company", "Dunzo", "Kronos", "Tekio", "Mmt", "Adobe", "Cadence", "Genpact", "Jp morgan", "Salesforce", "Visa", "Phonepe", "Mpl", "Deloitte", "Qualcomm", "Mind tree", "Zoho", "Atlassian", "Skack", "Ibm", "Oracle", "Dell", "Samsung", "Sap", "Goibibo", "Paytm", "Paypal", "Gojek", "Mckinsey", "Vedantu", "Sharechat", "Reliance jio", "Vmware", "Harness", "Kafka", "Morgan Stanley", "Nutanix", "Pratilipi", "Digital guardian", "Flobiz", "Grab", "Coforge ltd", "SanDisk", "Target", "Jaro tech", "Scaler", "McAfee", "Boeing", "Siemens", "Texas instruments", "Commvault", "Veeam", "Radware", "Amdocs", "Innovaccer", "Intuit", "Inmobi", "Curefit", "Infosys specialist programmer", "Arcesium", "Optum", "Publicis sapient", "Exl", "Sony", "Hughes systique technology (hst)", "Tata cliq", "Wells Fargo", "Sandive", "Global logic", "Bny melon", "Ion group"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditProfile.this, android.R.layout.select_dialog_item, ArrayCompany);
        s_dream_company.setAdapter(adapter2);

        saoi_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s_area_of_interest.getText().toString().isEmpty()) {
                    String name = s_area_of_interest.getText().toString();
                    saoi.add(name);
                    l_saoi.removeAllViews();
                    for (int i = 0; i < saoi.size(); i++) {
                        String data = saoi.get(i);
                        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(EditProfile.this);
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setLayoutParams(pa);
                        textView.setText(" " + data + " ");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                        pa.setMargins(5, 5, 5, 5);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setPadding(20, 10, 10, 10);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        l_saoi.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saoi.remove(data);
                                l_saoi.removeView(textView);

                            }
                        });
                        s_area_of_interest.setText("");


                    }
                }

            }
        });

        dream_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s_dream_company.getText().toString().isEmpty()) {
                    String name = s_dream_company.getText().toString();
                    Daoi.add(name);
                    l_dream.removeAllViews();
                    for (int i = 0; i < Daoi.size(); i++) {
                        String data = Daoi.get(i);
                        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(EditProfile.this);
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setLayoutParams(pa);
                        textView.setText(" " + data + " ");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                        pa.setMargins(5, 5, 5, 5);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setPadding(20, 10, 10, 10);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        l_dream.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Daoi.remove(data);
                                l_dream.removeView(textView);

                            }
                        });
                        s_dream_company.setText("");

                    }
                }

            }
        });

    }


    private void printAreaOFInterest() {
        for (int i = 0; i < saoi.size(); i++) {
            String data = saoi.get(i);
            LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(EditProfile.this);
            textView.setBackgroundResource(R.drawable.tag_bg);
            textView.setLayoutParams(pa);
            textView.setText(" " + data + " ");
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
            pa.setMargins(5, 5, 5, 5);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setPadding(20, 10, 10, 10);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            l_saoi.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saoi.remove(data);
                    l_saoi.removeView(textView);

                }
            });
            s_area_of_interest.setText("");


        }

    }
    private void printDreamOFInterest() {
        for (int i = 0; i < Daoi.size(); i++) {
            String data = Daoi.get(i);
            LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(EditProfile.this);
            textView.setBackgroundResource(R.drawable.tag_bg);
            textView.setLayoutParams(pa);
            textView.setText(" " + data + " ");
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
            pa.setMargins(5, 5, 5, 5);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setPadding(20, 10, 10, 10);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            l_dream.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Daoi.remove(data);
                    l_dream.removeView(textView);

                }
            });
            s_dream_company.setText("");


        }

    }

    private void sendChangedDataFirebase() {
        HashMap haspmap = new HashMap<>();
        haspmap.put("userName", user_name.getText().toString());
        haspmap.put("userEmail", user_email.getText().toString());

        HashMap hashMap2 = new HashMap();
        hashMap2.put("profession", user_profession.getText().toString());
        hashMap2.put("course", user_course.getText().toString());
        hashMap2.put("college_name", user_college.getText().toString());
        hashMap2.put("user_bio", user_bio.getText().toString());
        hashMap2.put("AreaOfInterest", saoi);
        hashMap2.put("CompanyName", Daoi);


        Ref.child("Users").child(CurrentUserId).updateChildren(haspmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Ref.child("UserDetails").child(CurrentUserId).updateChildren(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(EditProfile.this, "Data has been changed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void validation() {
        if (user_name.getText().toString().isEmpty() || user_email.getText().toString().isEmpty() || user_profession.getText().toString().isEmpty() || user_course.getText().toString().isEmpty() || user_college.getText().toString().isEmpty() || user_bio.getText().toString().isEmpty() || saoi.size() == 0 || Daoi.size() == 0) {
            errztext.setError("Please fill all the details");
            errztext.setText("Please fill some intrested fields");
        } else if (user_name.getText().toString().isEmpty()) {
            user_name.setError("Please fill profile name");
        } else if (user_email.getText().toString().isEmpty()) {
            user_email.setError("Please fill email");
        } else if (user_profession.getText().toString().isEmpty()) {
            user_profession.setError("Please choose profession");
        } else if (user_course.getText().toString().isEmpty()) {
            user_course.setError("Please fill course/degree");
        } else if (user_college.getText().toString().isEmpty()) {
            user_college.setError("Please fill your college name");
        } else if (user_bio.getText().toString().isEmpty()) {
            user_bio.setError("Please fill your profile heading");

        }else if (saoi.size() == 0){
            s_area_of_interest.setError("Please fill some intrested areas");
        }else if (Daoi.size() == 0){
            s_dream_company.setError("Please fill some dream companies");

        }
        else {
            isvalidation = true;
        }
    }


    private void  status (String status){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap map = new HashMap();
        map.put("status" , status);
        map.put("timeStamp" , timeStamp);

        ref.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}