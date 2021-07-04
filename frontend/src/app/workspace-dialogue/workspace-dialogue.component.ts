import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { microService } from '../microService';
import { ProfileSpecTO, PropertyDetail } from '../shared/models/ProfileSpecTO';
import { ConfigFilesService } from '../shared/shared-services/config-files.service';
import { ProfileAggregatorService } from '../shared/shared-services/profile-aggregator.service';
import { SprimeraFilesService } from '../shared/shared-services/sprimera-files.service';
import * as yaml from 'yaml';
import { CapService } from '../shared/shared-services/cap.service';
import { ResolveBranchInconsistencyService } from '../shared/shared-services/resolve-branch-inconsistency.service';
import { WarningDialogComponent } from '../shared/shared-components/warning-dialog/warning-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';

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
  branchValue: string = "";
  profileValue: string = "";

  isBranchReq = false;
  isProfileReq = false;
  canProfileDefault = false;

  displayData!: string;

  // Sprimera Variables
  propertyList: PropertyDetail[]=[];
  ownerList: string[] = [];

  // Variables for consistency across branches
  isBranchConsistency = false;
  destinationBranchValue: string = "";
  sourceBranchValue: string = "";
  isBranch1Req = false;
  isBranch2Req = false;

  // Variables for consistency across profile
  isProfileConsistency = false;  
  inconsistentProfileProperties = new Map();
  inconsistentProfiles: string[] = [];
  missingProperties: any[] = [];
  ICP: string = "";
  chosenMissingProperty: string = "";

  // Consistency variables
  tempSourceData!: string;
  sourceData!: string;
  MRDocuments: any[] = [];
  keepChanges = false;
  discardChanges = false;
  sendMR = false;

  visibleProgressSpinner = false;
  showBtn = false;
  reqValidation = true; // No error msg
  isEditable = false;
  
  constructor(@Inject(MAT_DIALOG_DATA) public data: microService,@Inject('WARNING_DIALOG_PARAM') private WARNING_DIALOG_PARAM: any,
  private _configFiles: ConfigFilesService, 
  private _sprimeraFilesService: SprimeraFilesService, private _profileAggregatorService: ProfileAggregatorService,
  private _capService: CapService, private _resolveBranchInconsistency: ResolveBranchInconsistencyService,
  public dialog: MatDialog, private _snackBar: MatSnackBar) {

    this.functionList = ["show individual file","sprimera",
    "consistency across branch","consistency across profile"];
    this.mservice = data;
    this.profileList = this.mservice.profiles.map((x: any) => x.name);
    this.branchList = this.mservice.branches.map((x:any) => x.name);
   }

  ngOnInit(): void {}

  setFunction(functionValue: any){
    this.displayData = "";
    this.propertyList = [];
    this.ownerList = [];
    this.functionValue = functionValue;
    this.showBtn = true;

    this.isBranchReq = false;
    this.isProfileReq = false;
    this.canProfileDefault = false;

    this.destinationBranchValue = "";
    this.sourceBranchValue = "";
    this.sourceData = "";
    this.isBranch1Req = false;
    this.isBranch2Req = false;
    this.tempSourceData = "";
    this.sourceData = "" ;
    this.MRDocuments = [];
    this.keepChanges = false;
    this.discardChanges = false;
    this.sendMR = false;

    this.isBranchConsistency = false;
    this.isProfileConsistency = false;
    
    this.missingProperties = [];
    this.reqValidation = true;
    this.isEditable = false;
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
      this.branchValue = ""; //branch is unset, want inconsistent profiles acc to branch.
    }
  }
  setBranch(branchValue: any){
    // cannot change branch directly in case of profile consistency function.
    if(this.keepChanges===true || this.MRDocuments.length>0){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.branchValue = branchValue;
          this.ICP = "";
          this.chosenMissingProperty = "";
          this.MRDocuments = [];
          this.sendMR = false

          this.discardChangesClicked();
          this.showInconsistentProfiles();
          this.visibleProgressSpinner = true; 
        }
        else{
          console.log("Branch should not be changed");
        }
      });
    }
    else{
      this.branchValue = branchValue;
      if(this.functionValue === "consistency across profile"){
        this.showInconsistentProfiles();
        this.visibleProgressSpinner = true;
      }
    }
    
  }

  setDestinationBranch(branchValue: any){
    if(this.keepChanges===true || this.MRDocuments.length>0){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.destinationBranchValue = branchValue;
          this.MRDocuments = [];
          this.sendMR = false;
          this.discardChangesClicked();
        }
        else{
          console.log("destinationBranch should not be changed");
        }
      });
    }
    else{
      this.destinationBranchValue = branchValue;
    }
    
  }
  setSourceBranch(branchValue: any){
    if(this.keepChanges===true || this.MRDocuments.length>0){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.sourceBranchValue = branchValue;
          this.MRDocuments = [];
          this.sendMR = false;
          this.discardChangesClicked();
        }
        else{
          console.log("sourceBranch should not be changed");
        }
      });
    }
    else{
      this.sourceBranchValue = branchValue;
    }
    
  }
  setProfile(profileValue: any){
    // profile cannot be changed directly in case of branch consistency function.
    if(this.functionValue==="consistency across branch" && this.keepChanges===true){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.profileValue = profileValue;
          this.sendMR = false;
          this.discardChangesClicked();
        }
        else{
          console.log("profile should not be changed");
        }
      });
    }
    else{
      this.profileValue = profileValue;
    }
  }
  
  //////// SHOWING INCONSISTENT PROFILES ////////////////////
  showInconsistentProfiles(){
    this.missingProperties = [];
    let profileAPIObject = {
      "services": [
          this.mservice.service
      ],
      "includeSuppressed": true,
       "email": {
          "sendEmail": true,
          "recipients": [
              "temp@gmail.com"
          ]
      }
    }
    this._capService.getReport(this.branchValue,profileAPIObject).subscribe((data: any[])=>{
      let actual_content = JSON.parse(JSON.stringify(data[0]));

      // Making the list of inconsistent profiles
      for(let i=0;i<actual_content["missingProperty"].length;i++){
        let profile = (actual_content["missingProperty"][i]["document"]["profile"]);
        let missingProperties = (actual_content["missingProperty"][i]["properties"]);
        this.inconsistentProfileProperties.set(profile,missingProperties);
      }
      this.inconsistentProfiles = Array.from( this.inconsistentProfileProperties.keys() );
      this.visibleProgressSpinner = false;

      if(this.inconsistentProfiles.length>0){
        this.isProfileConsistency = true;
      }
      else{
        this.displayData = "All profiles are consistent."
      }
    });
  }
  setICP(ICP: any){
    this.chosenMissingProperty = "";
    if(this.functionValue==="consistency across profile" && this.keepChanges===true){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.ICP = ICP;
          this.sendMR = false;
          this.discardChangesClicked();
        }
        else{
          console.log("ICP should not be changed");
        }
      });
    }
    else{
      this.ICP = ICP;
    }
  }
  
  populateMissingProperty(event: any){
    // Removing the chosen missing property from the list.
    let jsonDisplayData = yaml.parse(this.displayData);
    //this.chosenMissingProperty = event;
    this.chosenMissingProperty = event;

    let parentList = this.chosenMissingProperty.split(".");
    let curr = jsonDisplayData;
    for(let i=0;i<parentList.length-1;i++){
      if(!curr[parentList[i]]){
        curr[parentList[i]] = {};
      }
      curr = curr[parentList[i]];
    }
    curr[parentList[parentList.length-1]] = null;
    this.displayData = JSON.stringify(jsonDisplayData,null,2);

    // Removing the missing property from the list.
    let missingPropIdx = this.missingProperties.findIndex(x=>x===this.chosenMissingProperty);
    if(missingPropIdx!==-1){
      this.missingProperties.splice(missingPropIdx,1);
    }
    console.log(this.missingProperties);
    console.log(this.inconsistentProfileProperties.get(this.ICP));
    this.keepChanges = true;
    this.discardChanges = true;
    this.sendMR = false;
  }
  modifyProfileData(event: any){
    if(this.chosenMissingProperty!==""){
      this.tempSourceData = event;
    }
  }

   ////////////// RESOLVING BRANCH INCONSISTENCY ////////////////
  modifySourceData(event: any){
    this.tempSourceData = event;
    this.keepChanges = true;
    this.discardChanges = true;
    this.sendMR = false;
  }
  keepChangesClicked(){
    
    this.sourceData = this.tempSourceData;
    this.tempSourceData = "";
    this.keepChanges = false;
    this.discardChanges = false;

    let found = false;
    for(let i=0;i<this.MRDocuments.length;i++){
      if(this.isBranchConsistency){
        if(this.MRDocuments[i].profile===this.profileValue){
          this.MRDocuments[i].document = this.sourceData;
          found = true;
        }
      }
      else if(this.isProfileConsistency){
        this.inconsistentProfileProperties.set(this.ICP,this.missingProperties);
        //console.log(this.inconsistentProfileProperties.get(this.ICP));
        this.chosenMissingProperty = "";
        if(this.MRDocuments[i].profile===this.ICP){
          this.MRDocuments[i].document = this.sourceData;
          found = true;
        }
      }
    }
    if(found===false){
      if(this.isBranchConsistency){
        this.MRDocuments.push({
          "branch": this.sourceBranchValue,
          "profile": this.profileValue,
          "document": this.sourceData
        })
      }
      else if(this.isProfileConsistency){
        this.inconsistentProfileProperties.set(this.ICP,this.missingProperties);
        this.MRDocuments.push({
          "branch": this.branchValue,
          "profile": this.ICP,
          "document": this.sourceData
        })
      }
    }
    let simpleSnackBarRef = this._snackBar.open("changes saved locally");
    setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 3000);
    this.sendMR = true;
  }
  discardChangesClicked(){
    this.tempSourceData = "";
    this.discardChanges = false;
    this.keepChanges = false;
    this.sourceData = "";
    if(this.MRDocuments.length>0){
      this.sendMR = true;
    }
    if(this.isBranchConsistency)
    this.sendToCodeMirror();
    if(this.isProfileConsistency===true && this.ICP!==""){
      this.chosenMissingProperty = "";
      this.missingProperties = [];
      //when branch is changed we dont have ICP so we cannot send to codemirror.
      this.sendToCodeMirror();
    }
  }
  sendMergeRequest(){
    this.visibleProgressSpinner = true;
    let body: any;
    if(this.isBranchConsistency){
      body = {
        "service": this.mservice.service,
        "branch": this.sourceBranchValue,
        "documents": this.MRDocuments
      }
    }
    else if(this.isProfileConsistency){
      body = {
        "service": this.mservice.service,
        "branch": this.branchValue,
        "documents": this.MRDocuments
      }
    }
    this._resolveBranchInconsistency.sendMergeRequest(body).subscribe((data:any)=>{
      let responseList = data[0].split("\n");
      let mergeRequestMail = (responseList[2].trim()); // corresponds to email of the merge request.
      this.visibleProgressSpinner = false;
      this.MRDocuments = [];

      let temp = mergeRequestMail.split("pull/new");
      let temp2 = temp[0]+"compare/"+body["branch"]+".."+temp[1];
      window.open(temp2,"_blank");
    },
    err=>{
      let errorMsg = (err.error.error.errorMessage);
      let simpleSnackBarRef = this._snackBar.open(errorMsg,"Close");
      setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 100000);
      this.visibleProgressSpinner = false;
    }
    );
    this.sendMR = false;
  }

  processFunction(){
    this.reqValidation = true;
    if(this.isBranchReq===true && this.branchValue===""){
      this.reqValidation = false;
    }

    if(this.canProfileDefault===false && this.isProfileReq===true && this.profileValue===""){
      this.reqValidation = false;
    }
    if(this.isBranch1Req===true && this.sourceBranchValue===""){
      this.reqValidation = false;
    }
    if(this.isBranch2Req===true && this.destinationBranchValue===""){
      this.reqValidation = false;
    }
    if(this.isProfileConsistency===true && this.ICP===""){
      this.reqValidation = false;
    }
    if(this.reqValidation === true){
      this.sendToCodeMirror();
    } 
  }

  sendToCodeMirror(){
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
        this.visibleProgressSpinner = false;
        this.displayData = data;
      });
    }
 
    // SPIMERA
    else if(this.functionValue==="sprimera"){
      let profileSpecTOList: ProfileSpecTO[] = [];
      this._sprimeraFilesService.getFiles(this.mservice.service,this.branchValue,this.profileValue).subscribe((data: any[]) =>{
        //Converting the fetched files into the format required by profile_aggregator service.
        for(let i=0;i<data.length;i++){
          profileSpecTOList.push(new ProfileSpecTO(
            `${data[i].service}-${data[i].profile}`,
            data[i].yaml,
            data[i].jsonNode,
          ))
        }

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

    // CONSISTENCY ACROSS BRANCHES
    else if(this.functionValue==="consistency across branch"){

      this.isBranchConsistency = true;
      this._configFiles.getFile(this.mservice.service,this.functionValue, this.destinationBranchValue,this.profileValue)
      .subscribe(data => {
        // checkiong if the source data for this profile has some local changes.
        let found = false;
        for(let i=0;i<this.MRDocuments.length;i++){
          
          if(this.MRDocuments[i].profile===this.profileValue){
            this.sourceData = this.MRDocuments[i].document;
            found = true;
            this.visibleProgressSpinner = false;
          }
        }
        if(found===false){
          this._configFiles.getFile(this.mservice.service,this.functionValue, this.sourceBranchValue,this.profileValue)
          .subscribe(data2 => {  
            this.visibleProgressSpinner = false;
            this.sourceData = data2;
          });
        }
        this.displayData = data;
      }); 
    }
    //// CONSISTENCY ACROSS PROFILES
    else if(this.functionValue==="consistency across profile"){
      this._configFiles.getFile(this.mservice.service,"individual",this.branchValue,this.ICP)
      .subscribe(data => {
        this.isEditable = true;
        this.visibleProgressSpinner = false;

        let found = false;
        for(let i=0;i<this.MRDocuments.length;i++){
          
          if(this.MRDocuments[i].profile===this.ICP){
            this.displayData = this.MRDocuments[i].document;
            found = true;
            this.visibleProgressSpinner = false;
          }
        }
        if(found==false){
          this.displayData = data;
        }
        
        this.missingProperties = JSON.parse(JSON.stringify(this.inconsistentProfileProperties.get(this.ICP)));
      });
      
    }
  }
}

