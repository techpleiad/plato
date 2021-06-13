import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { microService } from '../microService';
import { ProfileSpecTO, PropertyDetail } from '../shared/models/ProfileSpecTO';
import { ConfigFilesService } from '../shared/shared-services/config-files.service';
import { ProfileAggregatorService } from '../shared/shared-services/profile-aggregator.service';
import { SprimeraFilesService } from '../shared/shared-services/sprimera-files.service';
import * as diff from 'deep-diff'
import * as yaml from 'yaml';
import { CapService } from '../shared/shared-services/cap.service';

@Component({
  selector: 'app-workspace-dialogue',
  templateUrl: './workspace-dialogue.component.html',
  styleUrls: ['./workspace-dialogue.component.css']
})
export class WorkspaceDialogueComponent implements OnInit {

  mservice:microService;

  profileList: string[] = [];
  branchList: string[] = [];
  functionList: string[] = [];
  
  functionValue: any;
  branchValue: any;
  profileValue: any;

  isBranchReq = false;
  isProfileReq = false;
  canProfileDefault = false;

  displayData!: string;

  // Variables for consistency across branches
  branch1Value: any;
  branch2Value: any;
  isBranch1Req = false;
  isBranch2Req = false;
  displayData2!: string;
  isBranchConsistency = false;

  // Variables for consistency across profile
  inconsistentProfileProperties = new Map();
  missingProperties: any[] = [];
  isProfileConsistency = false;

  inconsistentProfiles: string[] = [];
  ICP: string = "";

  // Sprimera Variables
  propertyList: PropertyDetail[]=[];
  ownerList: string[] = [];


  differenceProperties: string[] = [];
  //twoCodemirrors = false; //two codemirrors required while checking consistency.


  visibleProgressSpinner = false;
  

  constructor(@Inject(MAT_DIALOG_DATA) public data: microService, private _configFiles: ConfigFilesService, 
  private _sprimeraFilesService: SprimeraFilesService, private _profileAggregatorService: ProfileAggregatorService,
  private _capService: CapService) {
    this.functionList = ["show merged file","show individual file","sprimera",
    "consistency across branch","consistency across profile"];
    this.mservice = data;
    this.branchValue = "";
    this.profileValue = "";

    this.branch1Value = "";
    this.branch2Value = "";
 

    this.profileList = this.mservice.profiles.map((x: any) => x.name);
    this.branchList = this.mservice.branches.map((x:any) => x.name);
   }

  ngOnInit(): void {
  }
  setFunction(functionValue: any){
    this.functionValue = functionValue;
    this.isBranchReq = false;
    this.isProfileReq = false;
    this.canProfileDefault = false;

    this.branch1Value = "";
    this.branch2Value = "";
    this.displayData2 = "";
    this.isBranch1Req = false;
    this.isBranch2Req = false;

    this.isBranchConsistency = false;
    this.isProfileConsistency = false;
    

    this.missingProperties = [];
    
    this.setBranchProfileReq();
  }
  setBranchProfileReq(){
    if(this.functionValue==="show merged file" || this.functionValue==="show individual file" || this.functionValue=="sprimera"){
      this.isBranchReq = true;
      this.isProfileReq = true;
    }

    if(this.functionValue==="consistency across branch"){
      this.isBranch1Req = true;
      this.isBranch2Req = true;
      this.isProfileReq = true;
    }
  
    if(this.functionValue==="show individual file" || this.functionValue==="consistency across branch"){
      this.canProfileDefault = true;
    }
    if(this.functionValue==="consistency across profile"){
      this.isBranchReq = true;
    }
  }
  setBranch(branchValue: any){
    this.branchValue = branchValue;
    if(this.functionValue === "consistency across profile"){
      this.showInconsistentProfiles();
      this.visibleProgressSpinner = true;
    }
  }

  setBranch1(branchValue: any){
    this.branch1Value = branchValue;
  }
  setBranch2(branchValue: any){
    this.branch2Value = branchValue;
  }
  setProfile(profileValue: any){
    this.profileValue = profileValue;
  }
  setICP(ICP: any){
    this.ICP = ICP;
  }

  showInconsistentProfiles(){
    let tempObject = {
      "services": [
          this.mservice.service
      ],
      "includeSuppressed": true,
       "email": {
          "sendEmail": true,
          "recipients": [
              "abhishekgarg.14august@gmail.com"
          ]
      }
    }
    this._capService.getReport(this.branchValue,tempObject).subscribe((data: any[])=>{
      console.log(data[0]);
      let actual_content = JSON.parse(JSON.stringify(data[0]));

      // Making the list of inconsistent profiles
      for(let i=0;i<actual_content["missingProperty"].length;i++){
        let profile = (actual_content["missingProperty"][i]["document"]["profile"]);
        let missingProperties = (actual_content["missingProperty"][i]["properties"]);
        this.inconsistentProfileProperties.set(profile,missingProperties);
      }
      console.log("Incosistent Profile Properties Map");
      console.log(this.inconsistentProfileProperties);
      this.inconsistentProfiles = Array.from( this.inconsistentProfileProperties.keys() );
      console.log(this.inconsistentProfiles);
      this.visibleProgressSpinner = false;

      if(this.inconsistentProfiles.length>0){
        this.isProfileConsistency = true;
      }
      else{
        this.propertyList = [];
        this.ownerList = [];
        this.displayData = "All profiles are consistent."
      }

      this.visibleProgressSpinner = false;
    });
  }
  

  sendToCodeMirror(){
    // Progress Spinner 
    this.visibleProgressSpinner = true;

    // SHOW MERGED AND INDIVIDUAL FILES
    if(this.functionValue==="show merged file" || this.functionValue==="show individual file"){
      let type = "";
      if(this.functionValue==="show merged file"){
        type = "merged";
      }
      else{
        type = "individual";
      }
      this._configFiles.getFile(this.mservice.service,type, this.branchValue,this.profileValue)
      .subscribe(data => {
        this.propertyList = [];
        this.ownerList = [];
        this.visibleProgressSpinner = false;
        this.displayData = data;
      });
      
    }
    // CONSISTENCY ACROSS BRANCHES
    else if(this.functionValue==="consistency across branch"){

      this.isBranchConsistency = true;
      this._configFiles.getFile(this.mservice.service,this.functionValue, this.branch1Value,this.profileValue)
      .subscribe(data => {
        
        this._configFiles.getFile(this.mservice.service,this.functionValue, this.branch2Value,this.profileValue)
        .subscribe(data2 => {  
          this.visibleProgressSpinner = false;
          this.displayData2 = data2;
        });
        this.displayData = data;
      });
      
    }
 
    // SPIMERA
    else if(this.functionValue==="sprimera"){
      let profileSpecTOList: ProfileSpecTO[] = [];
      this._sprimeraFilesService.getFiles(this.mservice.service,this.branchValue,this.profileValue).subscribe((data: any[]) =>{
        console.log(data);
        //Converting the fetched files into the format required by profile_aggregator service.
        for(let i=0;i<data.length;i++){
          profileSpecTOList.push(new ProfileSpecTO(
            `${data[i].service}-${data[i].profile}`,
            data[i].yaml,
            data[i].jsonNode,
          ))
        }
        // Merging Files 
        const aggregated = this._profileAggregatorService.aggregateProfiles(profileSpecTOList);
        if(aggregated){
          this.visibleProgressSpinner = false;
        }
        this.displayData = JSON.stringify(aggregated.jsonContent,null,2);
        this.propertyList = aggregated.propertyList;
        this.ownerList = profileSpecTOList.map(function(val){
          return val.profile;
        })
      })
    }
    //// CONSISTENCY ACROSS PROFILES
    else if(this.functionValue==="consistency across profile"){
      this._configFiles.getFile(this.mservice.service,"individual",this.branchValue,this.profileValue)
      .subscribe(data => {
        this.visibleProgressSpinner = false;
        this.propertyList = [];
        this.ownerList = [];
        this.displayData = data;
        this.missingProperties = this.inconsistentProfileProperties.get(this.ICP);
      });
      
    }
  }
}
