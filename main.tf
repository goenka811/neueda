provider "aws" {
  region     = "eu-west-2"
  access_key = "AKIAQHVYFRX6TSQ2LBN7"
  secret_key = "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"
}
#resource "aws_instance" "myfirstserver" {
#  ami           = "ami-089539692cca55c6c"
#  instance_type = "t2.micro"
  //vpc_security_group_ids = ["${aws_security_group.instance.id}"]
  //tags = {Name = "main-example"}
  //user_data = <<-EOF #!/bin/bash yum -y java-1.8.0-openjdk-devel yum -y install tomcat systemctl enable tomcat systemctl start tomcat echo "welcome to tomcat, hello world" //etc/systemd/system/{{to muser}}.service EOF
  //tags = {
    //Name = "HelloWorld"
  //}
#}
#variable "subnet_prefix" {
#  description = "cidr block for the subnet"

#}


#resource "aws_vpc" "prod-vpc" {
#  cidr_block = "10.0.0.0/16"
#  tags = {
#    Name = "production"
#  }
#}

resource "aws_subnet" "subnet-1" {
  vpc_id            = aws_vpc.prod-vpc.id
  cidr_block        = "10.0.1.0/24" 
  availability_zone = "eu-west-2a"

  tags = {
    Name = "dev-subnet"
  }
}

resource "aws_subnet" "subnet-2" {
  vpc_id            = aws_vpc.prod-vpc.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "eu-west-2a"

  tags = {
    Name = "qa-subnet"
  }
}

 # 1. Create vpc

 resource "aws_vpc" "prod-vpc" {
   cidr_block = "10.0.0.0/16"
   tags = {
     Name = "production"
   }
 }

 # 2. Create Internet Gateway

 resource "aws_internet_gateway" "gw" {
   vpc_id = aws_vpc.prod-vpc.id


 }
# # 3. Create Custom Route Table

 resource "aws_route_table" "prod-route-table" {
   vpc_id = aws_vpc.prod-vpc.id

   route {
     cidr_block = "0.0.0.0/0"
     gateway_id = aws_internet_gateway.gw.id
   }

   route {
     ipv6_cidr_block = "::/0"
     gateway_id      = aws_internet_gateway.gw.id
   }

   tags = {
     Name = "Prod"
   }
 }

# # 4. Create a Subnet 

 resource "aws_subnet" "subnet-3" {
   vpc_id            = aws_vpc.prod-vpc.id
   cidr_block        = "10.0.3.0/24"
   availability_zone = "eu-west-2a"

   tags = {
     Name = "prod-subnet"
   }
 }

 # 5. Associate subnet with Route Table
 resource "aws_route_table_association" "a" {
   subnet_id      = aws_subnet.subnet-1.id
   route_table_id = aws_route_table.prod-route-table.id
 }
 # 6. Create Security Group to allow port 22,80,443
 resource "aws_security_group" "allow_web" {
   name        = "allow_web_traffic"
   description = "Allow Web inbound traffic"
   vpc_id      = aws_vpc.prod-vpc.id

   ingress {
     description = "HTTPS"
     from_port   = 443
     to_port     = 443
     protocol    = "tcp"
     cidr_blocks = ["0.0.0.0/0"]
   }
   ingress {
     description = "HTTP"
     from_port   = 80
     to_port     = 80
     protocol    = "tcp"
     cidr_blocks = ["0.0.0.0/0"]
   }
   ingress {
     description = "SSH"
     from_port   = 22
     to_port     = 22
     protocol    = "tcp"
     cidr_blocks = ["0.0.0.0/0"]
   }

   egress {
     from_port   = 0
     to_port     = 0
     protocol    = "-1"
     cidr_blocks = ["0.0.0.0/0"]
   }

   tags = {
     Name = "allow_web"
   }
 }

 # 7. Create a network interface with an ip in the subnet that was created in step 4

 resource "aws_network_interface" "web-server-nic" {
   subnet_id       = aws_subnet.subnet-1.id
   private_ips     = ["10.0.1.50"]
   security_groups = [aws_security_group.allow_web.id]

 }
# # 8. Assign an elastic IP to the network interface created in step 7

 resource "aws_eip" "one" {
   vpc                       = true
   network_interface         = aws_network_interface.web-server-nic.id
   associate_with_private_ip = "10.0.1.50"
   depends_on                = [aws_internet_gateway.gw]
 }

 output "server_public_ip" {
   value = aws_eip.one.public_ip
 }

 # 9. Create Ubuntu server and install/enable apache2

 resource "aws_instance" "web-server-instance" {
   ami               = "ami-0194c3e07668a7e36"
   instance_type     = "t2.micro"
   availability_zone = "eu-west-2a"
   key_name          = "rishsanj"

   network_interface {
     device_index         = 0
     network_interface_id = aws_network_interface.web-server-nic.id
   }

   user_data = <<-EOF
                 #!/bin/bash -xe
                 sudo apt update -y
                 sudo apt install apache2 -y
                 sudo systemctl start apache2
                 sudo bash -c 'echo your very first web server > /var/www/html/index.html'
                 EOF
   tags = {
     Name = "web-server"
   }
 }



 output "server_private_ip" {
   value = aws_instance.web-server-instance.private_ip

 }

 output "server_id" {
   value = aws_instance.web-server-instance.id
 }


# resource "<provider>_<resource_type>" "name" {
#     config options.....
#     key = "value"
#     key2 = "another value"
# }
