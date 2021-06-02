import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddNewServiceComponent } from './add-new-service/add-new-service.component';
import { AddServiceService } from './add-service.service';
import { microService } from './microService';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';
  constructor(private _addService: AddServiceService, public dialog: MatDialog){

  }
  openAddServiceDialog(){
    const dialogRef = this.dialog.open(AddNewServiceComponent,{
      //disableClose:true,
      width: "600px",
      minHeight: "300px"
    
    });

    dialogRef.afterClosed().subscribe((result: microService) => {
      console.log(result);
      if(result){
        this.addNewService(result);
      }
      else{
        console.log("null value");
      }
    });
  }

  addNewService(newService: microService){
    this._addService.addNewService(newService);
  }
}
